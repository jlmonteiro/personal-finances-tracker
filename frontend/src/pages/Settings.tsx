/**
 * Settings page for updating configuration and managing income sources.
 */
import { useState } from 'react'
import {
  Container,
  Title,
  Card,
  Select,
  NumberInput,
  Button,
  Group,
  Table,
  ActionIcon,
  Text,
  Divider,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { IconTrash, IconEdit } from '@tabler/icons-react'
import { updateConfiguration } from '../api/configuration'
import {
  listIncomeSources,
  createIncomeSource,
  updateIncomeSource,
  deleteIncomeSource,
  IncomeSourceResponse,
} from '../api/income-sources'
import { getErrorMessage } from '../api/client'
import { useConfiguration } from '../hooks/useConfiguration'
import { IncomeSourceForm, IncomeSourceFormValues } from '../components/IncomeSourceForm'

const CURRENCIES = [
  { value: 'EUR', label: 'EUR - Euro' },
  { value: 'GBP', label: 'GBP - British Pound' },
  { value: 'USD', label: 'USD - US Dollar' },
  { value: 'CHF', label: 'CHF - Swiss Franc' },
  { value: 'SEK', label: 'SEK - Swedish Krona' },
  { value: 'NOK', label: 'NOK - Norwegian Krone' },
  { value: 'DKK', label: 'DKK - Danish Krone' },
  { value: 'PLN', label: 'PLN - Polish Zloty' },
  { value: 'CZK', label: 'CZK - Czech Koruna' },
  { value: 'CAD', label: 'CAD - Canadian Dollar' },
  { value: 'AUD', label: 'AUD - Australian Dollar' },
  { value: 'BRL', label: 'BRL - Brazilian Real' },
]

export const Settings = () => {
  const queryClient = useQueryClient()
  const { data: config } = useConfiguration()
  const { data: incomeData } = useQuery({
    queryKey: ['income-sources'],
    queryFn: () => listIncomeSources(),
  })
  const [showIncomeForm, setShowIncomeForm] = useState(false)
  const [editingSource, setEditingSource] = useState<IncomeSourceResponse | null>(null)

  const configForm = useForm({
    initialValues: {
      currency: config?.currency ?? '',
      monthStartDay: config?.monthStartDay ?? 1,
    },
    validate: {
      currency: (v) => (v.length === 3 ? null : 'Currency is required'),
      monthStartDay: (v) => (v >= 1 && v <= 28 ? null : 'Must be between 1 and 28'),
    },
  })

  const handleSaveConfig = async () => {
    const validation = configForm.validate()
    if (validation.hasErrors) return

    try {
      await updateConfiguration({
        currency: configForm.values.currency,
        monthStartDay: configForm.values.monthStartDay,
      })
      await queryClient.invalidateQueries({ queryKey: ['configuration'] })
      notifications.show({
        title: 'Saved',
        message: 'Configuration updated.',
        color: 'green',
      })
    } catch (error) {
      notifications.show({
        title: 'Error',
        message: getErrorMessage(error),
        color: 'red',
      })
    }
  }

  const handleAddIncomeSource = async (values: IncomeSourceFormValues) => {
    try {
      await createIncomeSource({
        name: values.name,
        description: values.description || undefined,
        amount: { value: values.amount, currency: values.currency },
        frequency: values.frequency,
        paymentDateType: values.paymentDateType,
        paymentDateRule: values.paymentDateRule,
        startDate: values.startDate,
        endDate: values.endDate || undefined,
      })
      await queryClient.invalidateQueries({ queryKey: ['income-sources'] })
      setShowIncomeForm(false)
      notifications.show({ title: 'Saved', message: 'Income source added.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handleEditIncomeSource = async (values: IncomeSourceFormValues) => {
    if (!editingSource) return
    try {
      await updateIncomeSource(editingSource.id, {
        name: values.name,
        description: values.description || undefined,
        amount: { value: values.amount, currency: values.currency },
        frequency: values.frequency,
        paymentDateType: values.paymentDateType,
        paymentDateRule: values.paymentDateRule,
        startDate: values.startDate,
        endDate: values.endDate || undefined,
      })
      await queryClient.invalidateQueries({ queryKey: ['income-sources'] })
      setEditingSource(null)
      notifications.show({ title: 'Saved', message: 'Income source updated.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handleDeleteIncomeSource = async (id: string) => {
    try {
      await deleteIncomeSource(id)
      await queryClient.invalidateQueries({ queryKey: ['income-sources'] })
      notifications.show({ title: 'Deleted', message: 'Income source removed.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const incomeSources: IncomeSourceResponse[] = incomeData?.data ?? []

  return (
    <Container size="sm" py="xl">
      <Title order={1} mb="lg">Settings</Title>

      <Card shadow="sm" padding="lg" radius="md" withBorder mb="lg">
        <Title order={3} mb="md">Configuration</Title>
        <Select
          label="Currency"
          data={CURRENCIES}
          searchable
          mb="sm"
          {...configForm.getInputProps('currency')}
        />
        <NumberInput
          label="Month start day"
          description="Day of month (1-28)"
          min={1}
          max={28}
          mb="sm"
          {...configForm.getInputProps('monthStartDay')}
        />
        <Group justify="flex-end">
          <Button onClick={handleSaveConfig}>Save</Button>
        </Group>
      </Card>

      <Card shadow="sm" padding="lg" radius="md" withBorder>
        <Title order={3} mb="md">Income Sources</Title>

        {incomeSources.length > 0 ? (
          <Table mb="md">
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Name</Table.Th>
                <Table.Th ta="right">Amount</Table.Th>
                <Table.Th>Frequency</Table.Th>
                <Table.Th>Actions</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {incomeSources.map((source) => (
                <Table.Tr key={source.id}>
                  <Table.Td>{source.name}</Table.Td>
                  <Table.Td ta="right">
                    {source.amount.value} {source.amount.currency}
                  </Table.Td>
                  <Table.Td>{source.frequency}</Table.Td>
                  <Table.Td>
                    <Group gap="xs">
                      <ActionIcon
                        variant="subtle"
                        onClick={() => {
                          setEditingSource(source)
                          setShowIncomeForm(false)
                        }}
                        aria-label="Edit income source"
                      >
                        <IconEdit size={16} />
                      </ActionIcon>
                      <ActionIcon
                        color="red"
                        variant="subtle"
                        onClick={() => handleDeleteIncomeSource(source.id)}
                        aria-label="Delete income source"
                      >
                        <IconTrash size={16} />
                      </ActionIcon>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        ) : (
          <Text c="dimmed" mb="md">No income sources configured.</Text>
        )}

        <Divider mb="md" />

        {editingSource ? (
          <>
            <Text fw={500} mb="sm">Edit Income Source</Text>
            <IncomeSourceForm
              initialValues={{
                name: editingSource.name,
                description: editingSource.description ?? '',
                amount: editingSource.amount.value,
                currency: editingSource.amount.currency,
                frequency: editingSource.frequency,
                paymentDateType: editingSource.paymentDateType,
                paymentDateRule: editingSource.paymentDateRule,
                startDate: editingSource.startDate,
                endDate: editingSource.endDate ?? '',
              }}
              currencies={[config?.currency ?? 'EUR']}
              onSubmit={handleEditIncomeSource}
              onCancel={() => setEditingSource(null)}
            />
          </>
        ) : showIncomeForm ? (
          <IncomeSourceForm
            currencies={[config?.currency ?? 'EUR']}
            onSubmit={handleAddIncomeSource}
            onCancel={() => setShowIncomeForm(false)}
          />
        ) : (
          <Button variant="light" onClick={() => setShowIncomeForm(true)}>
            + Add Income Source
          </Button>
        )}
      </Card>
    </Container>
  )
}
