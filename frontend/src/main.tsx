import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { MantineProvider, createTheme } from '@mantine/core'
import { Notifications } from '@mantine/notifications'
import '@mantine/core/styles.css'
import '@mantine/notifications/styles.css'
import { App } from './App'

const theme = createTheme({
  primaryColor: 'blue',
  defaultRadius: 'md',
})

/**
 * Application entry point.
 * Sets up Mantine provider with theme, notifications, and renders the root component.
 */
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <MantineProvider theme={theme} defaultColorScheme="light">
      <Notifications />
      <App />
    </MantineProvider>
  </StrictMode>,
)
