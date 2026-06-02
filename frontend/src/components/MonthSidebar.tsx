/**
 * Shared sidebar for monthly views — income, remaining, category breakdown.
 */
import { Paper, Text, Group, ThemeIcon, Stack, Badge, List, RingProgress } from '@mantine/core'
import { IconWallet, IconCash, IconBuildingBank } from '@tabler/icons-react'
import { useQuery } from '@tanstack/react-query'
import { listIncomeSources } from '../api/income-sources'
import { listBankAccounts } from '../api/bank-accounts'
import { useConfiguration } from '../hooks/useConfiguration'
import { ExpenseResponse } from '../api/expenses'

const PIE_COLORS = ['blue', 'grape', 'teal', 'orange', 'pink', 'cyan', 'lime', 'violet', 'indigo', 'red']

interface MonthSidebarProps {
  expenses: ExpenseResponse[]
}

export const MonthSidebar = ({ expenses }: MonthSidebarProps) => {
  const { data: config } = useConfiguration()
  const currency = config?.currency ?? 'EUR'
  const fmt = (v: number) => new Intl.NumberFormat('en', { style: 'currency', currency }).format(v)

  const { data: incomeData } = useQuery({ queryKey: ['income-sources'], queryFn: () => listIncomeSources() })
  const { data: bankAccounts = [] } = useQuery({ queryKey: ['bank-accounts'], queryFn: listBankAccounts })
  const incomeSources = incomeData?.data ?? []
  const totalIncome = incomeSources.reduce((s, i) => s + parseFloat(i.amount.value), 0)
  const totalBudget = expenses.reduce((s, e) => s + e.expectedValue, 0)
  const remaining = totalIncome - totalBudget

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
      <Paper p="md" withBorder radius="md" shadow="xs">
        <Group gap="xs" mb="sm">
          <ThemeIcon variant="light" color="green" size="lg"><IconWallet size={20} /></ThemeIcon>
          <div>
            <Text size="xs" c="dimmed">Total Income</Text>
            <Text fw={700} size="lg">{fmt(totalIncome)}</Text>
          </div>
        </Group>
        <List size="xs" spacing={4}>
          {incomeSources.map((src) => (
            <List.Item key={src.id}>{src.name}: {fmt(parseFloat(src.amount.value))}</List.Item>
          ))}
        </List>
      </Paper>

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

      {bankAccounts.length > 0 && (
        <Paper p="md" withBorder radius="md" shadow="xs">
          <Group gap="xs" mb="sm">
            <ThemeIcon variant="light" color="violet" size="lg"><IconBuildingBank size={20} /></ThemeIcon>
            <Text size="sm" fw={500}>By Account</Text>
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
    </Stack>
  )
}
