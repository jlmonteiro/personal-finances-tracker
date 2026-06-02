/**
 * Reusable form for adding/editing income sources.
 * Used in both the setup wizard and settings page.
 */
import { Button, Group, Select, TextInput } from '@mantine/core'
import { useForm } from '@mantine/form'

export interface IncomeSourceFormValues {
  name: string
  description: string
  amount: string
  currency: string
  frequency: string
  paymentDateType: string
  paymentDateRule: string
  startDate: string
  endDate: string
}

interface IncomeSourceFormProps {
  initialValues?: Partial<IncomeSourceFormValues>
  currencies: string[]
  onSubmit: (values: IncomeSourceFormValues) => void
  onCancel?: () => void
  loading?: boolean
}

const FREQUENCIES = [
  { value: 'MONTHLY', label: 'Monthly' },
  { value: 'WEEKLY', label: 'Weekly' },
  { value: 'FORTNIGHTLY', label: 'Fortnightly' },
  { value: 'FOUR_WEEKLY', label: 'Four-Weekly' },
]

const DATE_TYPES = [
  { value: 'FIXED', label: 'Fixed day' },
  { value: 'RELATIVE', label: 'Relative' },
]

const RELATIVE_RULES = [
  { value: 'EVERY_MONDAY', label: 'Every Monday' },
  { value: 'EVERY_TUESDAY', label: 'Every Tuesday' },
  { value: 'EVERY_WEDNESDAY', label: 'Every Wednesday' },
  { value: 'EVERY_THURSDAY', label: 'Every Thursday' },
  { value: 'EVERY_FRIDAY', label: 'Every Friday' },
  { value: 'FIRST_MONDAY', label: 'First Monday of the month' },
  { value: 'FIRST_TUESDAY', label: 'First Tuesday of the month' },
  { value: 'FIRST_WEDNESDAY', label: 'First Wednesday of the month' },
  { value: 'FIRST_THURSDAY', label: 'First Thursday of the month' },
  { value: 'FIRST_FRIDAY', label: 'First Friday of the month' },
  { value: 'SECOND_MONDAY', label: 'Second Monday of the month' },
  { value: 'SECOND_TUESDAY', label: 'Second Tuesday of the month' },
  { value: 'SECOND_WEDNESDAY', label: 'Second Wednesday of the month' },
  { value: 'SECOND_THURSDAY', label: 'Second Thursday of the month' },
  { value: 'SECOND_FRIDAY', label: 'Second Friday of the month' },
  { value: 'LAST_MONDAY', label: 'Last Monday of the month' },
  { value: 'LAST_TUESDAY', label: 'Last Tuesday of the month' },
  { value: 'LAST_WEDNESDAY', label: 'Last Wednesday of the month' },
  { value: 'LAST_THURSDAY', label: 'Last Thursday of the month' },
  { value: 'LAST_FRIDAY', label: 'Last Friday of the month' },
]

const DAY_OPTIONS = Array.from({ length: 28 }, (_, i) => ({
  value: String(i + 1),
  label: String(i + 1),
}))

/**
 * Income source form component.
 * Renders fields for name, amount, frequency, and payment schedule.
 */
export const IncomeSourceForm = ({
  initialValues,
  currencies,
  onSubmit,
  onCancel,
  loading,
}: IncomeSourceFormProps) => {
  const form = useForm<IncomeSourceFormValues>({
    initialValues: {
      name: initialValues?.name ?? '',
      description: initialValues?.description ?? '',
      amount: initialValues?.amount ?? '',
      currency: initialValues?.currency ?? currencies[0] ?? '',
      frequency: initialValues?.frequency ?? 'MONTHLY',
      paymentDateType: initialValues?.paymentDateType ?? 'FIXED',
      paymentDateRule: initialValues?.paymentDateRule ?? '',
      startDate: initialValues?.startDate ?? new Date().toISOString().split('T')[0],
      endDate: initialValues?.endDate ?? '',
    },
    validate: {
      name: (v) => (v.trim() ? null : 'Name is required'),
      amount: (v) => (parseFloat(v) > 0 ? null : 'Amount must be greater than 0'),
      paymentDateRule: (v) => (v.trim() ? null : 'Payment date rule is required'),
    },
  })

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <TextInput label="Name" placeholder="e.g. Salary" mb="sm" {...form.getInputProps('name')} />
      <TextInput label="Description" placeholder="Optional" mb="sm" {...form.getInputProps('description')} />
      <Group grow mb="sm">
        <TextInput label="Amount" placeholder="e.g. 3500.00" {...form.getInputProps('amount')} />
        <Select
          label="Currency"
          data={currencies.map((c) => ({ value: c, label: c }))}
          {...form.getInputProps('currency')}
        />
      </Group>
      <Group grow mb="sm">
        <Select label="Frequency" data={FREQUENCIES} {...form.getInputProps('frequency')} />
        <Select label="Date type" data={DATE_TYPES} {...form.getInputProps('paymentDateType')} />
      </Group>
      {form.values.paymentDateType === 'FIXED' ? (
        <Select
          label="Day of month"
          placeholder="Select day"
          data={DAY_OPTIONS}
          searchable
          mb="sm"
          {...form.getInputProps('paymentDateRule')}
        />
      ) : (
        <Select
          label="Payment schedule"
          placeholder="Select when you get paid"
          data={RELATIVE_RULES}
          searchable
          mb="sm"
          {...form.getInputProps('paymentDateRule')}
        />
      )}
      <Group grow mb="sm">
        <TextInput label="Start date" type="date" {...form.getInputProps('startDate')} />
        <TextInput label="End date (optional)" type="date" {...form.getInputProps('endDate')} />
      </Group>
      <Group justify="flex-end" mt="md">
        {onCancel && (
          <Button variant="default" onClick={onCancel}>
            Cancel
          </Button>
        )}
        <Button type="submit" loading={loading}>
          Save
        </Button>
      </Group>
    </form>
  )
}
