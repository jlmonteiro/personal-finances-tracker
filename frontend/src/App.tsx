/**
 * Root application component.
 * Handles routing, navigation guard, and sidebar layout.
 */
import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom'
import { AppShell, NavLink, LoadingOverlay } from '@mantine/core'
import { IconDashboard, IconSettings, IconCategory, IconUsers, IconCalendar } from '@tabler/icons-react'
import { useConfiguration } from './hooks/useConfiguration'
import { Setup } from './pages/Setup'
import { Dashboard } from './pages/Dashboard'
import { Settings } from './pages/Settings'
import { Categories } from './pages/Categories'
import { Payees } from './pages/Payees'
import { Months } from './pages/Months'
import { MonthTable } from './pages/MonthTable'

export const App = () => {
  const { data: config, isLoading, isError } = useConfiguration()
  const navigate = useNavigate()
  const location = useLocation()

  if (isLoading) {
    return <LoadingOverlay visible />
  }

  // Redirect to setup if no configuration exists
  if (config === null || isError) {
    return (
      <Routes>
        <Route path="/setup" element={<Setup />} />
        <Route path="*" element={<Navigate to="/setup" replace />} />
      </Routes>
    )
  }

  return (
    <AppShell navbar={{ width: 220, breakpoint: 'sm' }} padding="md">
      <AppShell.Navbar p="sm">
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
          label="Settings"
          leftSection={<IconSettings size={20} />}
          active={location.pathname === '/settings'}
          onClick={() => navigate('/settings')}
        />
      </AppShell.Navbar>
      <AppShell.Main>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/months" element={<Months />} />
          <Route path="/month/:id" element={<MonthTable />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/payees" element={<Payees />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AppShell.Main>
    </AppShell>
  )
}
