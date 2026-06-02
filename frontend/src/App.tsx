/**
 * Root application component.
 * Handles routing, navigation guard, and sidebar layout.
 */
import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom'
import { AppShell, NavLink, LoadingOverlay } from '@mantine/core'
import { IconDashboard, IconSettings } from '@tabler/icons-react'
import { useConfiguration } from './hooks/useConfiguration'
import { Setup } from './pages/Setup'
import { Dashboard } from './pages/Dashboard'
import { Settings } from './pages/Settings'

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
          label="Settings"
          leftSection={<IconSettings size={20} />}
          active={location.pathname === '/settings'}
          onClick={() => navigate('/settings')}
        />
      </AppShell.Navbar>
      <AppShell.Main>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AppShell.Main>
    </AppShell>
  )
}
