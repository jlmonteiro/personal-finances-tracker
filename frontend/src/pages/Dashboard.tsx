/**
 * Dashboard page - home screen after setup is complete.
 * Shows financial overview (placeholder for now).
 */
import { Container, Title, Text } from '@mantine/core'

export const Dashboard = () => {
  return (
    <Container size="md" py="xl">
      <Title order={1}>Dashboard</Title>
      <Text mt="md" c="dimmed">
        Your financial overview will appear here. Start by planning your first month.
      </Text>
    </Container>
  )
}
