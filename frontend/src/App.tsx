import { Title, Text, Container } from '@mantine/core'

export function App() {
  return (
    <Container size="sm" py="xl">
      <Title order={1}>Personal Finances Tracker</Title>
      <Text mt="md">Application is running. Setup wizard coming soon.</Text>
    </Container>
  )
}
