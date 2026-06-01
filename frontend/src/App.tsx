import { Title, Text, Container } from '@mantine/core'

/**
 * Root application component.
 * Renders the main layout and will host routing once pages are implemented.
 */
export const App = () => {
  return (
    <Container size="sm" py="xl">
      <Title order={1}>Personal Finances Tracker</Title>
      <Text mt="md">Application is running. Setup wizard coming soon.</Text>
    </Container>
  )
}
