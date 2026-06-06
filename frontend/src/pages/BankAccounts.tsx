/**
 * Bank accounts management page.
 */
import { useState } from 'react'
import {
  Container,
  Title,
  Table,
  Button,
  Group,
  TextInput,
  Textarea,
  ActionIcon,
  Text,
  Modal,
  Avatar,
  FileButton,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { IconTrash, IconEdit, IconUpload } from '@tabler/icons-react'
import {
  listBankAccounts,
  createBankAccount,
  updateBankAccount,
  deleteBankAccount,
  getBankAccountLogoUrl,
  BankAccountResponse,
} from '../api/bank-accounts'
import { getErrorMessage } from '../api/client'

export const BankAccounts = () => {
  const queryClient = useQueryClient()
  const { data: accounts = [] } = useQuery({ queryKey: ['bank-accounts'], queryFn: listBankAccounts })
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<BankAccountResponse | null>(null)
  const [deleting, setDeleting] = useState<BankAccountResponse | null>(null)

  const form = useForm({
    initialValues: { name: '', description: '' },
    validate: { name: (v) => (v.trim() ? null : 'Name is required') },
  })

  const openCreate = () => { setEditing(null); form.reset(); setShowForm(true) }
  const openEdit = (a: BankAccountResponse) => {
    setEditing(a); form.setValues({ name: a.name, description: a.description ?? '' }); setShowForm(true)
  }

  const handleSubmit = async (values: { name: string; description: string }) => {
    try {
      if (editing) {
        await updateBankAccount(editing.id, { name: values.name, description: values.description || undefined })
      } else {
        await createBankAccount({ name: values.name, description: values.description || undefined })
      }
      await queryClient.invalidateQueries({ queryKey: ['bank-accounts'] })
      setShowForm(false)
      notifications.show({ title: 'Saved', message: editing ? 'Account updated.' : 'Account created.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteBankAccount(deleting.id)
      await queryClient.invalidateQueries({ queryKey: ['bank-accounts'] })
      notifications.show({ title: 'Deleted', message: 'Account removed.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    } finally { setDeleting(null) }
  }

  const handleLogoUpload = async (accountId: string, file: File | null) => {
    if (!file) return
    const formData = new FormData()
    formData.append('file', file)
    try {
      await fetch(`/api/v1/bank-accounts/${accountId}/logo`, { method: 'POST', body: formData })
      await queryClient.invalidateQueries({ queryKey: ['bank-accounts'] })
      notifications.show({ title: 'Saved', message: 'Logo uploaded.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  return (
    <Container size="sm" py="xl">
      <Group justify="space-between" mb="lg">
        <Title order={2}>Bank Accounts</Title>
        <Button onClick={openCreate}>+ Add Account</Button>
      </Group>

      {accounts.length > 0 ? (
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th w={50}>Logo</Table.Th>
              <Table.Th>Name</Table.Th>
              <Table.Th>Description</Table.Th>
              <Table.Th>Actions</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {accounts.map((a) => (
              <Table.Tr key={a.id}>
                <Table.Td>
                  <Avatar src={a.hasLogo ? getBankAccountLogoUrl(a.id) : null} size="sm" radius="xl">
                    {a.name[0]}
                  </Avatar>
                </Table.Td>
                <Table.Td>{a.name}</Table.Td>
                <Table.Td>{a.description ?? '—'}</Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <FileButton onChange={(file) => handleLogoUpload(a.id, file)} accept="image/*">
                      {(props) => (
                        <ActionIcon variant="subtle" {...props} aria-label="Upload logo">
                          <IconUpload size={16} />
                        </ActionIcon>
                      )}
                    </FileButton>
                    <ActionIcon variant="subtle" onClick={() => openEdit(a)} aria-label="Edit">
                      <IconEdit size={16} />
                    </ActionIcon>
                    <ActionIcon variant="subtle" color="red" onClick={() => setDeleting(a)} aria-label="Delete">
                      <IconTrash size={16} />
                    </ActionIcon>
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      ) : (
        <Text c="dimmed">No bank accounts yet. Add one to get started.</Text>
      )}

      <Modal opened={showForm} onClose={() => setShowForm(false)} title={editing ? 'Edit Account' : 'Add Account'}>
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <TextInput label="Name" placeholder="e.g. AIB Current" mb="sm" {...form.getInputProps('name')} />
          <Textarea label="Description" placeholder="Optional" mb="sm" {...form.getInputProps('description')} />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setShowForm(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </Group>
        </form>
      </Modal>

      <Modal opened={!!deleting} onClose={() => setDeleting(null)} title="Delete Account">
        <Text>Are you sure you want to delete "{deleting?.name}"?</Text>
        <Group justify="flex-end" mt="md">
          <Button variant="default" onClick={() => setDeleting(null)}>Cancel</Button>
          <Button color="red" onClick={handleDelete}>Delete</Button>
        </Group>
      </Modal>
    </Container>
  )
}
