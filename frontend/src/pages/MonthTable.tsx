/**
 * Monthly planning table view — expenses grouped by quarter.
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
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { IconTrash, IconCheck, IconArrowLeft, IconArrowRight } from '@tabler/icons-react'
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

const STATUS_COLORS: Record<string, string> = {
  PENDING: 'yellow',
  PAID: 'green',
  OVERDUE: 'red',
}

export const MonthTable = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showAddExpense, setShowAddExpense] = useState(false)
  const [payingExpense, setPayingExpense] = useState<ExpenseResponse | null>(null)

  const { data: month } = useQuery({
    queryKey: ['financial-month', id],
    queryFn: () => getFinancialMonth(id!),
    enabled: !!id,
  })
  const { data: quarters = [] } = useQuery({
    queryKey: ['quarters', id],
    queryFn: () => getQuarters(id!),
    enabled: !!id,
  })
  const { data: expenses = [] } = useQuery({
    queryKey: ['expenses', id],
    queryFn: () => listExpenses(id!),
    enabled: !!id,
  })
  const { data: months = [] } = useQuery({
    queryKey: ['financial-months'],
    queryFn: listFinancialMonths,
  })
  const { data: payees = [] } = useQuery({ queryKey: ['payees'], queryFn: listPayees })
  const { data: categories = [] } = useQuery({ queryKey: ['categories'], queryFn: listCategories })

  const payeeOptions = payees.map((p) => ({ value: p.id, label: p.name }))
  const categoryOptions = categories.map((c) => ({ value: c.id, label: c.name }))

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

  const payForm = useForm({ initialValues: { actualValue: '', paymentDate: '' } })

  const handleAddExpense = async (values: typeof addForm.values) => {
    try {
      await createExpense(id!, {
        payeeId: values.payeeId,
        categoryId: values.categoryId,
        title: values.title,
        expectedValue: parseFloat(values.expectedValue),
        dueDate: values.dueDate,
      })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setShowAddExpense(false)
      addForm.reset()
      notifications.show({ title: 'Saved', message: 'Expense added.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handlePay = async () => {
    if (!payingExpense) return
    try {
      await updateExpense(payingExpense.id, {
        actualValue: parseFloat(payForm.values.actualValue),
        paymentDate: payForm.values.paymentDate,
      })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setPayingExpense(null)
      payForm.reset()
      notifications.show({ title: 'Paid', message: 'Payment recorded.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handleDelete = async (expenseId: string) => {
    try {
      await deleteExpense(expenseId)
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  // Navigation
  const currentIdx = months.findIndex((m) => m.id === id)
  const prevMonth = currentIdx < months.length - 1 ? months[currentIdx + 1] : null
  const nextMonth = currentIdx > 0 ? months[currentIdx - 1] : null

  const monthLabel = month
    ? new Date(month.startDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long' })
    : ''

  return (
    <Container size="md" py="xl">
      <Group justify="space-between" mb="lg">
        <Group>
          <ActionIcon
            variant="subtle"
            disabled={!prevMonth}
            onClick={() => prevMonth && navigate(`/month/${prevMonth.id}`)}
            aria-label="Previous month"
          >
            <IconArrowLeft size={20} />
          </ActionIcon>
          <Title order={2}>{monthLabel}</Title>
          <ActionIcon
            variant="subtle"
            disabled={!nextMonth}
            onClick={() => nextMonth && navigate(`/month/${nextMonth.id}`)}
            aria-label="Next month"
          >
            <IconArrowRight size={20} />
          </ActionIcon>
        </Group>
        <Button onClick={() => setShowAddExpense(true)}>+ Add Expense</Button>
      </Group>

      <Tabs defaultValue="1">
        <Tabs.List mb="md">
          {quarters.map((q) => (
            <Tabs.Tab key={q.quarterNumber} value={String(q.quarterNumber)}>
              Q{q.quarterNumber}
            </Tabs.Tab>
          ))}
        </Tabs.List>

        {quarters.map((q) => {
          const quarterExpenses = expenses.filter((e) => e.quarterNumber === q.quarterNumber)
          const total = quarterExpenses.reduce((sum, e) => sum + e.expectedValue, 0)

          return (
            <Tabs.Panel key={q.quarterNumber} value={String(q.quarterNumber)}>
              <Text size="sm" c="dimmed" mb="sm">
                {q.startDate} → {q.endDate} · Budget: {total.toFixed(2)}
              </Text>
              {quarterExpenses.length > 0 ? (
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
                    {quarterExpenses.map((exp) => (
                      <Table.Tr key={exp.id}>
                        <Table.Td>{exp.title}</Table.Td>
                        <Table.Td>{exp.payee.name}</Table.Td>
                        <Table.Td ta="right">{exp.expectedValue.toFixed(2)}</Table.Td>
                        <Table.Td ta="right">{exp.actualValue?.toFixed(2) ?? '—'}</Table.Td>
                        <Table.Td>{exp.dueDate}</Table.Td>
                        <Table.Td>
                          <Badge color={STATUS_COLORS[exp.status]} size="sm">{exp.status}</Badge>
                        </Table.Td>
                        <Table.Td>
                          <Group gap="xs">
                            {exp.status !== 'PAID' && (
                              <ActionIcon
                                variant="subtle"
                                color="green"
                                onClick={() => {
                                  setPayingExpense(exp)
                                  payForm.setValues({
                                    actualValue: String(exp.expectedValue),
                                    paymentDate: new Date().toISOString().split('T')[0],
                                  })
                                }}
                                aria-label="Record payment"
                              >
                                <IconCheck size={16} />
                              </ActionIcon>
                            )}
                            <ActionIcon
                              variant="subtle"
                              color="red"
                              onClick={() => handleDelete(exp.id)}
                              aria-label="Delete"
                            >
                              <IconTrash size={16} />
                            </ActionIcon>
                          </Group>
                        </Table.Td>
                      </Table.Tr>
                    ))}
                  </Table.Tbody>
                </Table>
              ) : (
                <Text c="dimmed" size="sm">No expenses in this quarter.</Text>
              )}
            </Tabs.Panel>
          )
        })}
      </Tabs>

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
    </Container>
  )
}
