/**
 * Financial months list — create and navigate to month views.
 */
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Container,
  Title,
  Table,
  Button,
  Group,
  NumberInput,
  Modal,
  Text,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { listFinancialMonths, createFinancialMonth } from '../api/financial-months'
import { getErrorMessage } from '../api/client'

export const Months = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: months = [] } = useQuery({ queryKey: ['financial-months'], queryFn: listFinancialMonths })
  const [showCreate, setShowCreate] = useState(false)

  const now = new Date()
  const form = useForm({
    initialValues: { year: now.getFullYear(), month: now.getMonth() + 1 },
    validate: {
      year: (v) => (v >= 2020 && v <= 2100 ? null : 'Invalid year'),
      month: (v) => (v >= 1 && v <= 12 ? null : 'Invalid month'),
    },
  })

  const handleCreate = async (values: { year: number; month: number }) => {
    try {
      const created = await createFinancialMonth(values)
      await queryClient.invalidateQueries({ queryKey: ['financial-months'] })
      setShowCreate(false)
      navigate(`/month/${created.id}`)
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  return (
    <Container size="sm" py="xl">
      <Group justify="space-between" mb="lg">
        <Title order={2}>Financial Months</Title>
        <Button onClick={() => setShowCreate(true)}>+ New Month</Button>
      </Group>

      {months.length > 0 ? (
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>Month</Table.Th>
              <Table.Th>Period</Table.Th>
              <Table.Th />
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {months.map((m) => (
              <Table.Tr key={m.id} style={{ cursor: 'pointer' }} onClick={() => navigate(`/month/${m.id}`)}>
                <Table.Td>
                  {new Date(m.startDate).toLocaleDateString('en-US', { year: 'numeric', month: 'long' })}
                </Table.Td>
                <Table.Td>{m.startDate} → {m.endDate}</Table.Td>
                <Table.Td>
                  <Button variant="subtle" size="xs">View</Button>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      ) : (
        <Text c="dimmed">No financial months created yet.</Text>
      )}

      <Modal opened={showCreate} onClose={() => setShowCreate(false)} title="Create Financial Month">
        <form onSubmit={form.onSubmit(handleCreate)}>
          <Group grow mb="sm">
            <NumberInput label="Year" min={2020} max={2100} {...form.getInputProps('year')} />
            <NumberInput label="Month" min={1} max={12} {...form.getInputProps('month')} />
          </Group>
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setShowCreate(false)}>Cancel</Button>
            <Button type="submit">Create</Button>
          </Group>
        </form>
      </Modal>
    </Container>
  )
}
