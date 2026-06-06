/**
 * Root application component.
 * Handles routing, navigation guard, and sidebar layout.
 */
import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom'
import { AppShell, Group, NavLink, LoadingOverlay, Center, Stack, Text, Button } from '@mantine/core'
import { IconDashboard, IconSettings, IconCategory, IconUsers, IconCalendar, IconBuildingBank, IconAlertCircle } from '@tabler/icons-react'
import { useConfiguration } from './hooks/useConfiguration'
import { Setup } from './pages/Setup'
import { Dashboard } from './pages/Dashboard'
import { Settings } from './pages/Settings'
import { Categories } from './pages/Categories'
import { Payees } from './pages/Payees'
import { BankAccounts } from './pages/BankAccounts'
import { Months } from './pages/Months'
import { MonthTable } from './pages/MonthTable'
import { MonthCalendar } from './pages/MonthCalendar'

interface AppProps {
  embedded?: boolean
}

export const App = ({ embedded = false }: AppProps) => {
  const { data: config, isLoading, isError, refetch } = useConfiguration()
  const navigate = useNavigate()
  const location = useLocation()

  if (isLoading) {
    return <LoadingOverlay visible />
  }

  if (isError) {
    return (
      <Center h="100vh">
        <Stack align="center" gap="md">
          <IconAlertCircle size={48} color="var(--mantine-color-red-6)" />
          <Text size="lg" fw={500}>Unable to connect to the server</Text>
          <Text size="sm" c="dimmed">Please check that the backend is running and try again.</Text>
          <Button onClick={() => refetch()}>Retry</Button>
        </Stack>
      </Center>
    )
  }

  if (config === null) {
    return (
      <Routes>
        <Route path="/setup" element={<Setup />} />
        <Route path="*" element={<Navigate to="/setup" replace />} />
      </Routes>
    )
  }

  const navItems = (
    <>
      <NavLink
        label="Dashboard"
        leftSection={<IconDashboard size={20} />}
        active={location.pathname === '/'}
        onClick={() => navigate('/')}
      />
      <NavLink
        label="Monthly Planning"
        leftSection={<IconCalendar size={20} />}
        active={location.pathname.startsWith('/month')}
        onClick={() => navigate('/months')}
      />
      <NavLink
        label="Categories"
        leftSection={<IconCategory size={20} />}
        active={location.pathname === '/categories'}
        onClick={() => navigate('/categories')}
      />
      <NavLink
        label="Payees"
        leftSection={<IconUsers size={20} />}
        active={location.pathname === '/payees'}
        onClick={() => navigate('/payees')}
      />
      <NavLink
        label="Bank Accounts"
        leftSection={<IconBuildingBank size={20} />}
        active={location.pathname === '/bank-accounts'}
        onClick={() => navigate('/bank-accounts')}
      />
      <NavLink
        label="Settings"
        leftSection={<IconSettings size={20} />}
        active={location.pathname === '/settings'}
        onClick={() => navigate('/settings')}
      />
    </>
  )

  const routes = (
    <Routes>
      <Route path="/" element={<Dashboard />} />
      <Route path="/months" element={<Months />} />
      <Route path="/month/:id" element={<MonthTable />} />
      <Route path="/month/:id/calendar" element={<MonthCalendar />} />
      <Route path="/categories" element={<Categories />} />
      <Route path="/payees" element={<Payees />} />
      <Route path="/bank-accounts" element={<BankAccounts />} />
      <Route path="/settings" element={<Settings />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )

  if (embedded) {
    return (
      <Group align="flex-start" gap={0} wrap="nowrap">
        <Stack gap={0} p="xs" style={{ width: 220, minWidth: 220 }}>
          {navItems}
        </Stack>
        <div style={{ flex: 1, padding: 'var(--mantine-spacing-md)' }}>
          {routes}
        </div>
      </Group>
    )
  }

  return (
    <AppShell navbar={{ width: 220, breakpoint: 'sm' }} padding="md">
      <AppShell.Navbar p="sm">
        {navItems}
      </AppShell.Navbar>
      <AppShell.Main>
        {routes}
      </AppShell.Main>
    </AppShell>
  )
}
