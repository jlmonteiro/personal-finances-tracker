/**
 * Calendar view for a financial month.
 * Shows expenses on their due dates with quarter highlighting and status.
 */
import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Container,
  Title,
  Group,
  ActionIcon,
  SimpleGrid,
  Paper,
  Text,
  Badge,
  Stack,
  Modal,
  TextInput,
  Select,
  Button,
  Popover,
  Divider,
  Grid,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { IconArrowLeft, IconArrowRight, IconCircleCheck, IconPlus, IconEdit, IconTrash } from '@tabler/icons-react'
import { getFinancialMonth, getQuarters, listFinancialMonths } from '../api/financial-months'
import { listExpenses, createExpense, updateExpense, deleteExpense, ExpenseResponse } from '../api/expenses'
import { listPayees } from '../api/payees'
import { listCategories } from '../api/categories'
import { listBankAccounts } from '../api/bank-accounts'
import { useConfiguration } from '../hooks/useConfiguration'
import { getErrorMessage } from '../api/client'
import { MonthSidebar } from '../components/MonthSidebar'

const QUARTER_BG = ['blue.0', 'grape.0', 'teal.0', 'orange.0']

export const MonthCalendar = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: config } = useConfiguration()
  const currency = config?.currency ?? 'EUR'
  const [showAddExpense, setShowAddExpense] = useState(false)

  const formatValue = (value: number) =>
    new Intl.NumberFormat('en', { style: 'currency', currency }).format(value)

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
  const { data: bankAccounts = [] } = useQuery({ queryKey: ['bank-accounts'], queryFn: listBankAccounts })

  const addForm = useForm({
    initialValues: { payeeId: '', categoryId: '', title: '', expectedValue: '', dueDate: '', bankAccountId: '' },
    validate: {
      payeeId: (v) => (v ? null : 'Required'),
      categoryId: (v) => (v ? null : 'Required'),
      title: (v) => (v.trim() ? null : 'Required'),
      expectedValue: (v) => (parseFloat(v) > 0 ? null : 'Must be > 0'),
      dueDate: (v) => (v ? null : 'Required'),
      bankAccountId: (v) => (v ? null : 'Required'),
    },
  })

  const selectedPayee = payees.find((p) => p.id === addForm.values.payeeId)
  const payeeOptions = payees.map((p) => ({ value: p.id, label: p.name }))
  const bankAccountOptions = bankAccounts.map((a) => ({ value: a.id, label: a.name }))
  const categoryOptions = selectedPayee
    ? selectedPayee.categories.map((c) => ({ value: c.id, label: c.name }))
    : categories.map((c) => ({ value: c.id, label: c.name }))

  const openAddForDate = (date: string) => {
    addForm.reset()
    addForm.setFieldValue('dueDate', date)
    setShowAddExpense(true)
  }

  const handleAddExpense = async (values: typeof addForm.values) => {
    try {
      await createExpense(id!, {
        payeeId: values.payeeId,
        categoryId: values.categoryId,
        title: values.title,
        expectedValue: parseFloat(values.expectedValue),
        dueDate: values.dueDate,
        bankAccountId: values.bankAccountId,
      })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setShowAddExpense(false)
      addForm.reset()
      notifications.show({ title: 'Saved', message: 'Expense added.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const [editingExpense, setEditingExpense] = useState<ExpenseResponse | null>(null)
  const [deletingExpense, setDeletingExpense] = useState<ExpenseResponse | null>(null)
  const [openPopoverId, setOpenPopoverId] = useState<string | null>(null)
  const editForm = useForm({ initialValues: { title: '', expectedValue: '', dueDate: '' } })

  const openEdit = (exp: ExpenseResponse) => {
    setOpenPopoverId(null)
    setEditingExpense(exp)
    editForm.setValues({ title: exp.title, expectedValue: String(exp.expectedValue), dueDate: exp.dueDate })
  }

  const handleEdit = async () => {
    if (!editingExpense) return
    try {
      await updateExpense(editingExpense.id, {
        title: editForm.values.title,
        expectedValue: parseFloat(editForm.values.expectedValue),
        dueDate: editForm.values.dueDate,
      })
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      setEditingExpense(null)
      notifications.show({ title: 'Saved', message: 'Expense updated.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const confirmDelete = (exp: ExpenseResponse) => {
    setOpenPopoverId(null)
    setDeletingExpense(exp)
  }

  const handleDelete = async () => {
    if (!deletingExpense) return
    try {
      await deleteExpense(deletingExpense.id)
      await queryClient.invalidateQueries({ queryKey: ['expenses', id] })
      notifications.show({ title: 'Deleted', message: 'Expense removed.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    } finally {
      setDeletingExpense(null)
    }
  }

  const currentIdx = months.findIndex((m) => m.id === id)
  const prevMonth = currentIdx < months.length - 1 ? months[currentIdx + 1] : null
  const nextMonth = currentIdx > 0 ? months[currentIdx - 1] : null

  const monthLabel = month
    ? new Date(month.startDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long' })
    : ''

  // Build calendar days
  const days: { date: string; quarterNumber: number; expenses: ExpenseResponse[] }[] = []
  if (month) {
    const start = new Date(month.startDate)
    const end = new Date(month.endDate)
    for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
      const dateStr = d.toISOString().split('T')[0]
      const quarter = quarters.find((q) => dateStr >= q.startDate && dateStr <= q.endDate)
      days.push({
        date: dateStr,
        quarterNumber: quarter?.quarterNumber ?? 1,
        expenses: expenses.filter((e) => e.dueDate === dateStr),
      })
    }
  }

  return (
    <Container size="xl" py="xl">
      <Group justify="space-between" mb="lg">
        <Group>
          <ActionIcon
            variant="subtle"
            disabled={!prevMonth}
            onClick={() => prevMonth && navigate(`/month/${prevMonth.id}/calendar`)}
            aria-label="Previous month"
          >
            <IconArrowLeft size={20} />
          </ActionIcon>
          <Title order={2}>{monthLabel}</Title>
          <ActionIcon
            variant="subtle"
            disabled={!nextMonth}
            onClick={() => nextMonth && navigate(`/month/${nextMonth.id}/calendar`)}
            aria-label="Next month"
          >
            <IconArrowRight size={20} />
          </ActionIcon>
        </Group>
        <Group gap="xs">
          <Badge variant="light" size="sm" style={{ cursor: 'pointer' }} onClick={() => navigate(`/month/${id}`)}>
            Table View
          </Badge>
          {[1, 2, 3, 4].map((q) => (
            <Badge key={q} variant="light" color={QUARTER_BG[q - 1].split('.')[0]} size="sm">
              Q{q}
            </Badge>
          ))}
        </Group>
      </Group>

      <Grid>
        <Grid.Col span={3}>
          <MonthSidebar expenses={expenses} />
        </Grid.Col>
        <Grid.Col span={9}>
      {/* Overall stats */}
      {expenses.length > 0 && (() => {
        const totalBudget = expenses.reduce((s, e) => s + e.expectedValue, 0)
        const totalSpent = expenses.reduce((s, e) => s + (e.actualValue ?? 0), 0)
        return (
          <SimpleGrid cols={3} mb="md">
            <Paper p="sm" withBorder radius="md" shadow="xs">
              <Text size="xs" c="dimmed">Budget</Text>
              <Text fw={700}>{formatValue(totalBudget)}</Text>
            </Paper>
            <Paper p="sm" withBorder radius="md" shadow="xs">
              <Text size="xs" c="dimmed">Spent</Text>
              <Text fw={700}>{formatValue(totalSpent)}</Text>
            </Paper>
            <Paper p="sm" withBorder radius="md" shadow="xs">
              <Text size="xs" c="dimmed">Remaining</Text>
              <Text fw={700} c={totalBudget - totalSpent >= 0 ? 'teal' : 'red'}>{formatValue(totalBudget - totalSpent)}</Text>
            </Paper>
          </SimpleGrid>
        )
      })()}

      <SimpleGrid cols={7} spacing="xs">
        {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((d) => (
          <Text key={d} ta="center" fw={700} size="sm" c="dimmed">{d}</Text>
        ))}

        {/* Pad start to correct weekday */}
        {days.length > 0 && (() => {
          const firstDay = new Date(days[0].date).getDay()
          const pad = firstDay === 0 ? 6 : firstDay - 1 // Monday = 0
          return Array.from({ length: pad }, (_, i) => <div key={`pad-${i}`} />)
        })()}

        {days.map((day) => (
          <Paper
            key={day.date}
            p="xs"
            bg={QUARTER_BG[day.quarterNumber - 1]}
            withBorder
            style={{ minHeight: 80 }}
          >
            <Group justify="space-between" mb={2}>
              <Text size="xs" fw={500}>
                {new Date(day.date).getDate()}
              </Text>
              <ActionIcon size="xs" variant="subtle" onClick={() => openAddForDate(day.date)} aria-label="Add expense">
                <IconPlus size={12} />
              </ActionIcon>
            </Group>
            <Stack gap={2}>
              {day.expenses.map((exp) => (
                <Popover key={exp.id} width={250} position="bottom" shadow="md" opened={openPopoverId === exp.id} onChange={(opened) => setOpenPopoverId(opened ? exp.id : null)}>
                  <Popover.Target>
                    <Paper p={4} withBorder radius="sm" bg="white" shadow="xs" style={{ cursor: 'pointer' }} onClick={() => setOpenPopoverId(openPopoverId === exp.id ? null : exp.id)}>
                      <Group gap={4} wrap="nowrap">
                        {exp.status === 'PAID' && <IconCircleCheck size={14} color="green" />}
                        <Text size="xs" truncate style={{ flex: 1 }}>
                          {exp.title}
                        </Text>
                        <Text size="xs" fw={600} c="dimmed">
                          {formatValue(exp.expectedValue)}
                        </Text>
                      </Group>
                    </Paper>
                  </Popover.Target>
                  <Popover.Dropdown>
                    <Text size="sm" fw={600} mb={4}>{exp.title}</Text>
                    <Text size="xs" c="dimmed">Payee: {exp.payee.name}</Text>
                    <Text size="xs" c="dimmed">Category: {exp.category.name}</Text>
                    <Text size="xs" c="dimmed">Expected: {formatValue(exp.expectedValue)}</Text>
                    {exp.actualValue && <Text size="xs" c="dimmed">Actual: {formatValue(exp.actualValue)}</Text>}
                    <Text size="xs" c="dimmed">Due: {exp.dueDate}</Text>
                    {exp.paymentDate && <Text size="xs" c="dimmed">Paid: {exp.paymentDate}</Text>}
                    <Text size="xs" c="dimmed">Status: {exp.status}</Text>
                    {exp.description && <Text size="xs" c="dimmed" mt={4}>{exp.description}</Text>}
                    <Divider my="sm" />
                    <Group gap="xs">
                      <Button size="xs" variant="light" leftSection={<IconEdit size={14} />} onClick={() => openEdit(exp)}>
                        Edit
                      </Button>
                      <Button size="xs" variant="light" color="red" leftSection={<IconTrash size={14} />} onClick={() => confirmDelete(exp)}>
                        Delete
                      </Button>
                    </Group>
                  </Popover.Dropdown>
                </Popover>
              ))}
            </Stack>
          </Paper>
        ))}
      </SimpleGrid>
        </Grid.Col>
      </Grid>

      <Modal opened={showAddExpense} onClose={() => setShowAddExpense(false)} title="Add Expense">
        <form onSubmit={addForm.onSubmit(handleAddExpense)}>
          <TextInput label="Title" mb="sm" {...addForm.getInputProps('title')} />
          <Select label="Payee" data={payeeOptions} searchable mb="sm" {...addForm.getInputProps('payeeId')} />
          <Select label="Category" data={categoryOptions} searchable mb="sm" {...addForm.getInputProps('categoryId')} />
          <TextInput label="Expected value" mb="sm" {...addForm.getInputProps('expectedValue')} />
          <TextInput label="Due date" type="date" mb="sm" {...addForm.getInputProps('dueDate')} />
          <Select label="Bank Account" data={bankAccountOptions} mb="sm" {...addForm.getInputProps('bankAccountId')} />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setShowAddExpense(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
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

      <Modal opened={!!deletingExpense} onClose={() => setDeletingExpense(null)} title="Delete Expense">
        <Text>Are you sure you want to delete "{deletingExpense?.title}"?</Text>
        <Group justify="flex-end" mt="md">
          <Button variant="default" onClick={() => setDeletingExpense(null)}>Cancel</Button>
          <Button color="red" onClick={handleDelete}>Delete</Button>
        </Group>
      </Modal>
    </Container>
  )
}
