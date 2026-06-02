/**
 * Shared sidebar for monthly views — income entries, remaining, category breakdown, account reservations.
 */
import { useState } from 'react'
import { Paper, Text, Group, ThemeIcon, Stack, Badge, List, RingProgress, ActionIcon, Modal, TextInput, Button } from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { IconWallet, IconCash, IconBuildingBank, IconEdit, IconPlus, IconTrash } from '@tabler/icons-react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { listIncomeEntries, createIncomeEntry, updateIncomeEntry, deleteIncomeEntry, IncomeEntryResponse } from '../api/income-entries'
import { listBankAccounts } from '../api/bank-accounts'
import { useConfiguration } from '../hooks/useConfiguration'
import { ExpenseResponse } from '../api/expenses'
import { getErrorMessage } from '../api/client'

const PIE_COLORS = ['blue', 'grape', 'teal', 'orange', 'pink', 'cyan', 'lime', 'violet', 'indigo', 'red']

interface MonthSidebarProps {
  monthId: string
  expenses: ExpenseResponse[]
}

export const MonthSidebar = ({ monthId, expenses }: MonthSidebarProps) => {
  const queryClient = useQueryClient()
  const { data: config } = useConfiguration()
  const currency = config?.currency ?? 'EUR'
  const fmt = (v: number) => new Intl.NumberFormat('en', { style: 'currency', currency }).format(v)

  const { data: incomeEntries = [] } = useQuery({
    queryKey: ['income-entries', monthId],
    queryFn: () => listIncomeEntries(monthId),
    enabled: !!monthId,
  })
  const { data: bankAccounts = [] } = useQuery({ queryKey: ['bank-accounts'], queryFn: listBankAccounts })

  const totalIncome = incomeEntries.reduce((s, e) => s + e.amount, 0)
  const totalBudget = expenses.reduce((s, e) => s + e.expectedValue, 0)
  const remaining = totalIncome - totalBudget

  const [editingEntry, setEditingEntry] = useState<IncomeEntryResponse | null>(null)
  const [showAdd, setShowAdd] = useState(false)

  const form = useForm({ initialValues: { name: '', amount: '' } })

  const handleSave = async () => {
    try {
      if (editingEntry) {
        await updateIncomeEntry(editingEntry.id, { name: form.values.name, amount: parseFloat(form.values.amount) })
      } else {
        await createIncomeEntry(monthId, { name: form.values.name, amount: parseFloat(form.values.amount) })
      }
      await queryClient.invalidateQueries({ queryKey: ['income-entries', monthId] })
      setEditingEntry(null)
      setShowAdd(false)
      form.reset()
    } catch (error) { notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' }) }
  }

  const handleDelete = async (id: string) => {
    await deleteIncomeEntry(id)
    await queryClient.invalidateQueries({ queryKey: ['income-entries', monthId] })
  }

  // Category breakdown
  const categoryTotals = new Map<string, { name: string; total: number }>()
  for (const exp of expenses) {
    const existing = categoryTotals.get(exp.category.id)
    if (existing) existing.total += exp.expectedValue
    else categoryTotals.set(exp.category.id, { name: exp.category.name, total: exp.expectedValue })
  }
  const pieData = [...categoryTotals.values()].sort((a, b) => b.total - a.total)
  const pieTotal = pieData.reduce((s, d) => s + d.total, 0)

  return (
    <Stack gap="md">
      {/* Income */}
      <Paper p="md" withBorder radius="md" shadow="xs">
        <Group justify="space-between" mb="sm">
          <Group gap="xs">
            <ThemeIcon variant="light" color="green" size="lg"><IconWallet size={20} /></ThemeIcon>
            <div>
              <Text size="xs" c="dimmed">Total Income</Text>
              <Text fw={700} size="lg">{fmt(totalIncome)}</Text>
            </div>
          </Group>
          <ActionIcon size="sm" variant="subtle" onClick={() => { setShowAdd(true); setEditingEntry(null); form.reset() }} aria-label="Add income">
            <IconPlus size={14} />
          </ActionIcon>
        </Group>
        <List size="xs" spacing={4} listStyleType="none">
          {incomeEntries.length === 0 && (
            <Button size="xs" variant="light" fullWidth mb="xs" onClick={async () => {
              try {
                // Trigger generation by calling POST with a dummy then refresh
                await fetch(`/api/v1/financial-months/${monthId}/income-entries/generate`, { method: 'POST' })
                await queryClient.invalidateQueries({ queryKey: ['income-entries', monthId] })
              } catch { /* fallback below */ }
            }}>
              Sync from Income Sources
            </Button>
          )}
          {incomeEntries.map((entry) => (
            <List.Item key={entry.id}>
              <Group gap={4} justify="space-between">
                <Text size="xs">{entry.name}{entry.isAdhoc ? ' ✱' : ''}</Text>
                <Group gap={2}>
                  <Text size="xs" fw={500}>{fmt(entry.amount)}</Text>
                  <ActionIcon size="xs" variant="subtle" onClick={() => { setEditingEntry(entry); form.setValues({ name: entry.name, amount: String(entry.amount) }); setShowAdd(true) }}>
                    <IconEdit size={10} />
                  </ActionIcon>
                  {entry.isAdhoc && (
                    <ActionIcon size="xs" variant="subtle" color="red" onClick={() => handleDelete(entry.id)}>
                      <IconTrash size={10} />
                    </ActionIcon>
                  )}
                </Group>
              </Group>
            </List.Item>
          ))}
        </List>
      </Paper>

      {/* Remaining */}
      <Paper p="md" withBorder radius="md" shadow="xs">
        <Group gap="xs">
          <ThemeIcon variant="light" color={remaining >= 0 ? 'teal' : 'red'} size="lg"><IconCash size={20} /></ThemeIcon>
          <div>
            <Text size="xs" c="dimmed">Expected Remaining</Text>
            <Text fw={700} size="lg" c={remaining >= 0 ? 'teal' : 'red'}>{fmt(remaining)}</Text>
          </div>
        </Group>
        <Text size="xs" c="dimmed" mt="xs">{fmt(totalIncome)} income − {fmt(totalBudget)} budget</Text>
      </Paper>

      {/* Pie chart */}
      {pieData.length > 0 && (
        <Paper p="md" withBorder radius="md" shadow="xs">
          <Text size="sm" fw={500} mb="sm">By Category</Text>
          <RingProgress
            size={160}
            thickness={20}
            roundCaps
            sections={pieData.map((d, i) => ({
              value: pieTotal > 0 ? (d.total / pieTotal) * 100 : 0,
              color: PIE_COLORS[i % PIE_COLORS.length],
            }))}
            label={<Text ta="center" size="xs" fw={700}>{fmt(pieTotal)}</Text>}
          />
          <Stack gap={4} mt="sm">
            {pieData.map((d, i) => (
              <Group key={d.name} gap="xs">
                <Badge size="xs" color={PIE_COLORS[i % PIE_COLORS.length]} variant="filled" circle> </Badge>
                <Text size="xs" style={{ flex: 1 }}>{d.name}</Text>
                <Text size="xs" fw={500}>{fmt(d.total)}</Text>
              </Group>
            ))}
          </Stack>
        </Paper>
      )}

      {/* By Account */}
      {bankAccounts.length > 0 && (
        <Paper p="md" withBorder radius="md" shadow="xs">
          <Group gap="xs" mb="sm">
            <ThemeIcon variant="light" color="violet" size="lg"><IconBuildingBank size={20} /></ThemeIcon>
            <Text size="sm" fw={500}>Funds Required</Text>
          </Group>
          <Stack gap={4}>
            {bankAccounts.map((account) => {
              const reserved = expenses
                .filter((e) => e.bankAccountId === account.id && e.status !== 'PAID')
                .reduce((s, e) => s + e.expectedValue, 0)
              return (
                <Group key={account.id} gap="xs">
                  <Text size="xs" style={{ flex: 1 }}>{account.name}</Text>
                  <Text size="xs" fw={700} c={reserved > 0 ? 'violet' : 'dimmed'}>{fmt(reserved)}</Text>
                </Group>
              )
            })}
          </Stack>
        </Paper>
      )}

      {/* Income Modal */}
      <Modal opened={showAdd} onClose={() => { setShowAdd(false); setEditingEntry(null) }} title={editingEntry ? 'Edit Income' : 'Add Income'}>
        <form onSubmit={form.onSubmit(handleSave)}>
          <TextInput label="Name" placeholder="e.g. Overtime, Freelance" mb="sm" {...form.getInputProps('name')} />
          <TextInput label="Amount" mb="sm" {...form.getInputProps('amount')} />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => { setShowAdd(false); setEditingEntry(null) }}>Cancel</Button>
            <Button type="submit">Save</Button>
          </Group>
        </form>
      </Modal>
    </Stack>
  )
}
