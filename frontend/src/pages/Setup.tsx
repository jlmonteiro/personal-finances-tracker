/**
 * Setup wizard page for first-time application configuration.
 * Steps: 1) Currency, 2) Month start day, 3) Income sources.
 */
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Container,
  Stepper,
  Button,
  Group,
  Select,
  NumberInput,
  Title,
  Text,
  Card,
  Table,
  ActionIcon,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQueryClient } from '@tanstack/react-query'
import { IconTrash } from '@tabler/icons-react'
import { createConfiguration } from '../api/configuration'
import { createIncomeSource, IncomeSourceResponse } from '../api/income-sources'
import { getErrorMessage } from '../api/client'
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

export const Setup = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [active, setActive] = useState(0)
  const [loading, setLoading] = useState(false)
  const [incomeSources, setIncomeSources] = useState<IncomeSourceResponse[]>([])
  const [showIncomeForm, setShowIncomeForm] = useState(false)

  const form = useForm({
    initialValues: {
      currency: '',
      monthStartDay: 1,
    },
    validate: {
      currency: (value) => (value.length === 3 ? null : 'Currency is required'),
      monthStartDay: (value) =>
        value >= 1 && value <= 28 ? null : 'Must be between 1 and 28',
    },
  })

  const nextStep = () => {
    if (active === 0) {
      const errors = form.validateField('currency')
      if (errors.hasError) return
    }
    if (active === 1) {
      const errors = form.validateField('monthStartDay')
      if (errors.hasError) return
    }
    setActive((prev) => prev + 1)
  }

  const prevStep = () => setActive((prev) => prev - 1)

  const handleAddIncomeSource = async (values: IncomeSourceFormValues) => {
    try {
      const created = await createIncomeSource({
        name: values.name,
        description: values.description || undefined,
        amount: { value: values.amount, currency: values.currency },
        frequency: values.frequency,
        paymentDateType: values.paymentDateType,
        paymentDateRule: values.paymentDateRule,
        startDate: values.startDate,
        endDate: values.endDate || undefined,
      })
      setIncomeSources((prev) => [...prev, created])
      setShowIncomeForm(false)
    } catch (error) {
      notifications.show({
        title: 'Error',
        message: getErrorMessage(error),
        color: 'red',
      })
    }
  }

  const handleFinish = async () => {
    setLoading(true)
    try {
      await createConfiguration({
        currency: form.values.currency,
        monthStartDay: form.values.monthStartDay,
      })
      await queryClient.invalidateQueries({ queryKey: ['configuration'] })
      notifications.show({
        title: 'Setup complete',
        message: 'Your configuration has been saved.',
        color: 'green',
      })
      navigate('/')
    } catch (error) {
      notifications.show({
        title: 'Error',
        message: getErrorMessage(error),
        color: 'red',
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Container size="sm" py="xl">
      <Card shadow="sm" padding="lg" radius="md" withBorder>
        <Title order={2} ta="center" mb="lg">
          Personal Finances Tracker
        </Title>

        <Stepper active={active} mb="xl">
          <Stepper.Step label="Currency" description="Select currency">
            <Text mb="md" fw={500}>
              Select your currency
            </Text>
            <Select
              label="Currency"
              placeholder="Choose your currency"
              data={CURRENCIES}
              searchable
              {...form.getInputProps('currency')}
            />
          </Stepper.Step>

          <Stepper.Step label="Month Start" description="Financial month">
            <Text mb="md" fw={500}>
              When does your financial month start?
            </Text>
            <NumberInput
              label="Day of month"
              description="Select a day between 1 and 28"
              min={1}
              max={28}
              {...form.getInputProps('monthStartDay')}
            />
          </Stepper.Step>

          <Stepper.Step label="Income" description="Income sources">
            <Text mb="md" fw={500}>
              Add your income sources
            </Text>

            {incomeSources.length > 0 && (
              <Table mb="md">
                <Table.Thead>
                  <Table.Tr>
                    <Table.Th>Name</Table.Th>
                    <Table.Th>Amount</Table.Th>
                    <Table.Th>Frequency</Table.Th>
                    <Table.Th>Actions</Table.Th>
                  </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                  {incomeSources.map((source) => (
                    <Table.Tr key={source.id}>
                      <Table.Td>{source.name}</Table.Td>
                      <Table.Td>
                        {source.amount.value} {source.amount.currency}
                      </Table.Td>
                      <Table.Td>{source.frequency}</Table.Td>
                      <Table.Td>
                        <ActionIcon
                          color="red"
                          variant="subtle"
                          onClick={() =>
                            setIncomeSources((prev) => prev.filter((s) => s.id !== source.id))
                          }
                          aria-label="Remove income source"
                        >
                          <IconTrash size={16} />
                        </ActionIcon>
                      </Table.Td>
                    </Table.Tr>
                  ))}
                </Table.Tbody>
              </Table>
            )}

            {showIncomeForm ? (
              <IncomeSourceForm
                currencies={[form.values.currency || 'EUR']}
                onSubmit={handleAddIncomeSource}
                onCancel={() => setShowIncomeForm(false)}
              />
            ) : (
              <Button variant="light" onClick={() => setShowIncomeForm(true)}>
                + Add Income Source
              </Button>
            )}
          </Stepper.Step>
        </Stepper>

        <Group justify="space-between" mt="xl">
          <div>
            {active > 0 && (
              <Button variant="default" onClick={prevStep}>
                Back
              </Button>
            )}
          </div>
          <div>
            {active < 2 ? (
              <Button onClick={nextStep}>Next</Button>
            ) : (
              <Button onClick={handleFinish} loading={loading}>
                Finish Setup
              </Button>
            )}
          </div>
        </Group>
      </Card>
    </Container>
  )
}
