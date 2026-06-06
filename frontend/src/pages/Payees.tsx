/**
 * Payees management page.
 */
import { useState } from 'react'
import {
  Container,
  Title,
  Table,
  Button,
  Group,
  TextInput,
  MultiSelect,
  ActionIcon,
  Text,
  Badge,
  Modal,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { IconTrash, IconEdit } from '@tabler/icons-react'
import { listCategories } from '../api/categories'
import { listPayees, createPayee, updatePayee, deletePayee, PayeeResponse } from '../api/payees'
import { getErrorMessage } from '../api/client'

export const Payees = () => {
  const queryClient = useQueryClient()
  const { data: payees = [] } = useQuery({ queryKey: ['payees'], queryFn: listPayees })
  const { data: categories = [] } = useQuery({ queryKey: ['categories'], queryFn: listCategories })
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<PayeeResponse | null>(null)
  const [deleting, setDeleting] = useState<PayeeResponse | null>(null)

  const categoryOptions = categories.map((c) => ({ value: c.id, label: c.name }))

  const form = useForm({
    initialValues: { name: '', categoryIds: [] as string[] },
    validate: {
      name: (v) => (v.trim() ? null : 'Name is required'),
      categoryIds: (v) => (v.length > 0 ? null : 'At least one category is required'),
    },
  })

  const openEdit = (payee: PayeeResponse) => {
    setEditing(payee)
    form.setValues({ name: payee.name, categoryIds: payee.categories.map((c) => c.id) })
    setShowForm(true)
  }

  const openCreate = () => {
    setEditing(null)
    form.reset()
    setShowForm(true)
  }

  const handleSubmit = async (values: { name: string; categoryIds: string[] }) => {
    try {
      if (editing) {
        await updatePayee(editing.id, values)
        notifications.show({ title: 'Saved', message: 'Payee updated.', color: 'green' })
      } else {
        await createPayee(values)
        notifications.show({ title: 'Saved', message: 'Payee created.', color: 'green' })
      }
      await queryClient.invalidateQueries({ queryKey: ['payees'] })
      setShowForm(false)
      form.reset()
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deletePayee(deleting.id)
      await queryClient.invalidateQueries({ queryKey: ['payees'] })
      notifications.show({ title: 'Deleted', message: 'Payee removed.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    } finally {
      setDeleting(null)
    }
  }

  return (
    <Container size="sm" py="xl">
      <Group justify="space-between" mb="lg">
        <Title order={2}>Payees</Title>
        <Button onClick={openCreate}>+ Add Payee</Button>
      </Group>

      {payees.length > 0 ? (
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>Name</Table.Th>
              <Table.Th>Categories</Table.Th>
              <Table.Th>Actions</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {payees.map((payee) => (
              <Table.Tr key={payee.id}>
                <Table.Td>{payee.name}</Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    {payee.categories.map((c) => (
                      <Badge key={c.id} variant="light" size="sm">{c.name}</Badge>
                    ))}
                  </Group>
                </Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <ActionIcon variant="subtle" onClick={() => openEdit(payee)} aria-label="Edit">
                      <IconEdit size={16} />
                    </ActionIcon>
                    <ActionIcon variant="subtle" color="red" onClick={() => setDeleting(payee)} aria-label="Delete">
                      <IconTrash size={16} />
                    </ActionIcon>
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      ) : (
        <Text c="dimmed">No payees yet. Add one to get started.</Text>
      )}

      <Modal opened={showForm} onClose={() => setShowForm(false)} title={editing ? 'Edit Payee' : 'Add Payee'}>
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <TextInput label="Name" placeholder="e.g. Dunnes Stores" mb="sm" {...form.getInputProps('name')} />
          <MultiSelect
            label="Categories"
            placeholder="Select categories"
            data={categoryOptions}
            mb="sm"
            {...form.getInputProps('categoryIds')}
          />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setShowForm(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </Group>
        </form>
      </Modal>

      <Modal opened={!!deleting} onClose={() => setDeleting(null)} title="Delete Payee">
        <Text>Are you sure you want to delete "{deleting?.name}"?</Text>
        <Group justify="flex-end" mt="md">
          <Button variant="default" onClick={() => setDeleting(null)}>Cancel</Button>
          <Button color="red" onClick={handleDelete}>Delete</Button>
        </Group>
      </Modal>
    </Container>
  )
}
