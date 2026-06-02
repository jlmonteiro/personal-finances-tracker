/**
 * Monthly planning table view — left income panel + right quarter expense view.
 */
import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Container,
  Title,
  Table,
  Badge,
  Group,
  Button,
  Text,
  ActionIcon,
  Modal,
  TextInput,
  Select,
  Tabs,
  Paper,
  SimpleGrid,
  Accordion,
  ThemeIcon,
  Grid,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import {
  IconTrash,
  IconCheck,
  IconArrowLeft,
  IconArrowRight,
  IconEdit,
  IconReceipt,
} from '@tabler/icons-react'
import * as TablerIcons from '@tabler/icons-react'
import {
  getFinancialMonth,
  getQuarters,
  listFinancialMonths,
} from '../api/financial-months'
import {
  listExpenses,
  createExpense,
  updateExpense,
  deleteExpense,
  ExpenseResponse,
} from '../api/expenses'
import { listPayees } from '../api/payees'
import { listCategories } from '../api/categories'
import { getErrorMessage } from '../api/client'
import { useConfiguration } from '../hooks/useConfiguration'
import { MonthSidebar } from '../components/MonthSidebar'

const STATUS_COLORS: Record<string, string> = { PENDING: 'yellow', PAID: 'green', OVERDUE: 'red' }

const resolveIcon = (name: string, size = 18) => {
  const pascalName = 'Icon' + name.split('-').map((s) => s.charAt(0).toUpperCase() + s.slice(1)).join('')
  const Icon = (TablerIcons as Record<string, unknown>)[pascalName] as React.FC<{ size?: number }> | undefined
  return Icon ? <Icon size={size} /> : null
}

export const MonthTable = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: config } = useConfiguration()
  const currency = config?.currency ?? 'EUR'
  const fmt = (v: number) => new Intl.NumberFormat('en', { style: 'currency', currency }).format(v)

  const [showAddExpense, setShowAddExpense] = useState(false)
  const [payingExpense, setPayingExpense] = useState<ExpenseResponse | null>(null)
  const [editingExpense, setEditingExpense] = useState<ExpenseResponse | null>(null)

  const { data: month } = useQuery({ queryKey: ['financial-month', id], queryFn: () => getFinancialMonth(id!), enabled: !!id })
  const { data: quarters = [] } = useQuery({ queryKey: ['quarters', id], queryFn: () => getQuarters(id!), enabled: !!id })
  const { data: expenses = [] } = useQuery({ queryKey: ['expenses', id], queryFn: () => listExpenses(id!), enabled: !!id })
  const { data: months = [] } = useQuery({ queryKey: ['financial-months'], queryFn: listFinancialMonths })
  const { data: payees = [] } = useQuery({ queryKey: ['payees'], queryFn: listPayees })
  const { data: categories = [] } = useQuery({ queryKey: ['categories'], queryFn: listCategories })

  const addForm = useForm({
    initialValues: { payeeId: '', categoryId: '', title: '', expectedValue: '', dueDate: '' },
    validate: {
      payeeId: (v) => (v ? null : 'Required'),
      categoryId: (v) => (v ? null : 'Required'),
      title: (v) => (v.trim() ? null : 'Required'),
      expectedValue: (v) => (parseFloat(v) > 0 ? null : 'Must be > 0'),
      dueDate: (v) => (v ? null : 'Required'),
    },
  })
  const selectedPayee = payees.find((p) => p.id === addForm.values.payeeId)
  const categoryOptions = selectedPayee
    ? selectedPayee.categories.map((c) => ({ value: c.id, label: c.name }))
    : categories.map((c) => ({ value: c.id, label: c.name }))
  const payeeOptions = payees.map((p) => ({ value: p.id, label: p.name }))

  const payForm = useForm({ initialValues: { actualValue: '', paymentDate: '' } })
  const editForm = useForm({ initialValues: { title: '', expectedValue: '', dueDate: '' } })

  const handleAddExpense = async (values: typeof addForm.values) => {
    try {
      await createExpense(id!, { payeeId: values.payeeId, categoryId: values.categoryId, title: values.title, expectedValue: parseFloat(values.expectedValue), dueDate: values.dueDate })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setShowAddExpense(false)
      addForm.reset()
      notifications.show({ title: 'Saved', message: 'Expense added.', color: 'green' })
    } catch (error) { notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' }) }
  }

  const handlePay = async () => {
    if (!payingExpense) return
    try {
      await updateExpense(payingExpense.id, { actualValue: parseFloat(payForm.values.actualValue), paymentDate: payForm.values.paymentDate })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setPayingExpense(null)
      payForm.reset()
      notifications.show({ title: 'Paid', message: 'Payment recorded.', color: 'green' })
    } catch (error) { notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' }) }
  }

  const handleDelete = async (expenseId: string) => {
    try { await deleteExpense(expenseId); await queryClient.invalidateQueries({ queryKey: ['expenses', id] }) }
    catch (error) { notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' }) }
  }

  const openEdit = (exp: ExpenseResponse) => {
    setEditingExpense(exp)
    editForm.setValues({ title: exp.title, expectedValue: String(exp.expectedValue), dueDate: exp.dueDate })
  }

  const handleEdit = async () => {
    if (!editingExpense) return
    try {
      await updateExpense(editingExpense.id, { title: editForm.values.title, expectedValue: parseFloat(editForm.values.expectedValue), dueDate: editForm.values.dueDate })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setEditingExpense(null)
      notifications.show({ title: 'Saved', message: 'Expense updated.', color: 'green' })
    } catch (error) { notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' }) }
  }

  // Navigation
  const currentIdx = months.findIndex((m) => m.id === id)
  const prevMonth = currentIdx < months.length - 1 ? months[currentIdx + 1] : null
  const nextMonth = currentIdx > 0 ? months[currentIdx - 1] : null
  const monthLabel = month ? new Date(month.startDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long' }) : ''

  return (
    <Container size="xl" py="xl">
      {/* Header */}
      <Group justify="space-between" mb="lg">
        <Group>
          <ActionIcon variant="subtle" disabled={!prevMonth} onClick={() => prevMonth && navigate(`/month/${prevMonth.id}`)} aria-label="Previous month"><IconArrowLeft size={20} /></ActionIcon>
          <Title order={2}>{monthLabel}</Title>
          <ActionIcon variant="subtle" disabled={!nextMonth} onClick={() => nextMonth && navigate(`/month/${nextMonth.id}`)} aria-label="Next month"><IconArrowRight size={20} /></ActionIcon>
        </Group>
        <Group>
          <Button variant="light" size="xs" onClick={() => navigate(`/month/${id}/calendar`)}>Calendar</Button>
          <Button onClick={() => setShowAddExpense(true)}>+ Add Expense</Button>
        </Group>
      </Group>

      <Grid>
        {/* Left Panel */}
        <Grid.Col span={3}>
          <MonthSidebar expenses={expenses} />
        </Grid.Col>

        {/* Right Content */}
        <Grid.Col span={9}>
          <Tabs defaultValue="1">
            <Tabs.List mb="md">
              {quarters.map((q) => {
                const qExpenses = expenses.filter((e) => e.quarterNumber === q.quarterNumber)
                const unpaid = qExpenses.filter((e) => e.status !== 'PAID').reduce((s, e) => s + e.expectedValue, 0)
                const allPaid = qExpenses.length > 0 && qExpenses.every((e) => e.status === 'PAID')
                return (
                  <Tabs.Tab key={q.quarterNumber} value={String(q.quarterNumber)}>
                    <Group gap={4}>
                      {allPaid && <IconCheck size={14} color="green" />}
                      <span>Q{q.quarterNumber} · {unpaid > 0 ? fmt(unpaid) : '✓'}</span>
                    </Group>
                  </Tabs.Tab>
                )
              })}
            </Tabs.List>

            {quarters.map((q) => {
              const quarterExpenses = expenses.filter((e) => e.quarterNumber === q.quarterNumber)
              const qBudget = quarterExpenses.reduce((s, e) => s + e.expectedValue, 0)
              const qActual = quarterExpenses.reduce((s, e) => s + (e.actualValue ?? 0), 0)

              // Group by category
              const grouped = new Map<string, { name: string; icon: string; expenses: ExpenseResponse[] }>()
              for (const exp of quarterExpenses) {
                if (!grouped.has(exp.category.id)) grouped.set(exp.category.id, { name: exp.category.name, icon: exp.category.icon, expenses: [] })
                grouped.get(exp.category.id)!.expenses.push(exp)
              }

              return (
                <Tabs.Panel key={q.quarterNumber} value={String(q.quarterNumber)}>
                  {/* Quarter stats */}
                  <SimpleGrid cols={3} mb="md">
                    <Paper p="sm" withBorder radius="md" shadow="xs">
                      <Text size="xs" c="dimmed">Budget</Text>
                      <Text fw={700}>{fmt(qBudget)}</Text>
                    </Paper>
                    <Paper p="sm" withBorder radius="md" shadow="xs">
                      <Text size="xs" c="dimmed">Spent</Text>
                      <Text fw={700}>{fmt(qActual)}</Text>
                    </Paper>
                    <Paper p="sm" withBorder radius="md" shadow="xs">
                      <Text size="xs" c="dimmed">Remaining</Text>
                      <Text fw={700} c={qBudget - qActual >= 0 ? 'teal' : 'red'}>{fmt(qBudget - qActual)}</Text>
                    </Paper>
                  </SimpleGrid>

                  <Text size="sm" c="dimmed" mb="sm">{q.startDate} → {q.endDate}</Text>

                  {quarterExpenses.length > 0 ? (
                    <Accordion multiple defaultValue={[...grouped.keys()]}>
                      {[...grouped.entries()].map(([catId, group]) => {
                        const catTotal = group.expenses.reduce((s, e) => s + e.expectedValue, 0)
                        return (
                          <Accordion.Item key={catId} value={catId}>
                            <Accordion.Control>
                              <Group gap="sm">
                                <ThemeIcon variant="light" size="sm" radius="xl">
                                  {resolveIcon(group.icon, 14) ?? <IconReceipt size={14} />}
                                </ThemeIcon>
                                <Text fw={500}>{group.name}</Text>
                                <Badge variant="light" size="sm">{fmt(catTotal)}</Badge>
                                <Badge variant="dot" size="sm">{group.expenses.length} items</Badge>
                              </Group>
                            </Accordion.Control>
                            <Accordion.Panel>
                              <Table>
                                <Table.Thead>
                                  <Table.Tr>
                                    <Table.Th>Title</Table.Th>
                                    <Table.Th>Payee</Table.Th>
                                    <Table.Th ta="right">Expected</Table.Th>
                                    <Table.Th ta="right">Actual</Table.Th>
                                    <Table.Th>Due</Table.Th>
                                    <Table.Th>Status</Table.Th>
                                    <Table.Th>Actions</Table.Th>
                                  </Table.Tr>
                                </Table.Thead>
                                <Table.Tbody>
                                  {group.expenses.map((exp) => (
                                    <Table.Tr key={exp.id}>
                                      <Table.Td>{exp.title}</Table.Td>
                                      <Table.Td>{exp.payee.name}</Table.Td>
                                      <Table.Td ta="right">{fmt(exp.expectedValue)}</Table.Td>
                                      <Table.Td ta="right">{exp.actualValue ? fmt(exp.actualValue) : '—'}</Table.Td>
                                      <Table.Td>{exp.dueDate}</Table.Td>
                                      <Table.Td><Badge color={STATUS_COLORS[exp.status]} size="sm">{exp.status}</Badge></Table.Td>
                                      <Table.Td>
                                        <Group gap="xs">
                                          <ActionIcon variant="subtle" onClick={() => openEdit(exp)} aria-label="Edit"><IconEdit size={16} /></ActionIcon>
                                          {exp.status !== 'PAID' ? (
                                            <ActionIcon variant="subtle" color="green" onClick={() => { setPayingExpense(exp); payForm.setValues({ actualValue: String(exp.expectedValue), paymentDate: new Date().toISOString().split('T')[0] }) }} aria-label="Record payment"><IconCheck size={16} /></ActionIcon>
                                          ) : (
                                            <ActionIcon variant="subtle" color="orange" onClick={async () => { await updateExpense(exp.id, { clearPayment: true }); await queryClient.invalidateQueries({ queryKey: ['expenses', id] }) }} aria-label="Undo payment"><IconArrowLeft size={16} /></ActionIcon>
                                          )}
                                          <ActionIcon variant="subtle" color="red" onClick={() => handleDelete(exp.id)} aria-label="Delete"><IconTrash size={16} /></ActionIcon>
                                        </Group>
                                      </Table.Td>
                                    </Table.Tr>
                                  ))}
                                </Table.Tbody>
                              </Table>
                            </Accordion.Panel>
                          </Accordion.Item>
                        )
                      })}
                    </Accordion>
                  ) : (
                    <Text c="dimmed" size="sm">No expenses in this quarter.</Text>
                  )}
                </Tabs.Panel>
              )
            })}
          </Tabs>
        </Grid.Col>
      </Grid>

      {/* Modals */}
      <Modal opened={showAddExpense} onClose={() => setShowAddExpense(false)} title="Add Expense">
        <form onSubmit={addForm.onSubmit(handleAddExpense)}>
          <TextInput label="Title" mb="sm" {...addForm.getInputProps('title')} />
          <Select label="Payee" data={payeeOptions} searchable mb="sm" {...addForm.getInputProps('payeeId')} />
          <Select label="Category" data={categoryOptions} searchable mb="sm" {...addForm.getInputProps('categoryId')} />
          <TextInput label="Expected value" mb="sm" {...addForm.getInputProps('expectedValue')} />
          <TextInput label="Due date" type="date" mb="sm" {...addForm.getInputProps('dueDate')} />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setShowAddExpense(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </Group>
        </form>
      </Modal>

      <Modal opened={!!payingExpense} onClose={() => setPayingExpense(null)} title="Record Payment">
        <form onSubmit={payForm.onSubmit(handlePay)}>
          <TextInput label="Actual amount" mb="sm" {...payForm.getInputProps('actualValue')} />
          <TextInput label="Payment date" type="date" mb="sm" {...payForm.getInputProps('paymentDate')} />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setPayingExpense(null)}>Cancel</Button>
            <Button color="green" type="submit">Confirm Payment</Button>
          </Group>
        </form>
      </Modal>

      <Modal opened={!!editingExpense} onClose={() => setEditingExpense(null)} title="Edit Expense">
        <form onSubmit={editForm.onSubmit(handleEdit)}>
          <TextInput label="Title" mb="sm" {...editForm.getInputProps('title')} />
          <TextInput label="Expected value" mb="sm" {...editForm.getInputProps('expectedValue')} />
          <TextInput label="Due date" type="date" mb="sm" {...editForm.getInputProps('dueDate')} />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setEditingExpense(null)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </Group>
        </form>
      </Modal>
    </Container>
  )
}
