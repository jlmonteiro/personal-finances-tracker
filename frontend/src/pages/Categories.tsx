/**
 * Categories management page.
 */
import { useState } from 'react'
import {
  Container,
  Title,
  Table,
  Button,
  Group,
  TextInput,
  ActionIcon,
  Text,
  Modal,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { IconTrash, IconEdit } from '@tabler/icons-react'
import * as TablerIcons from '@tabler/icons-react'
import {
  listCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  CategoryResponse,
} from '../api/categories'
import { getErrorMessage } from '../api/client'

/** Resolves a kebab-case icon name to a Tabler icon component. */
const resolveIcon = (name: string) => {
  const pascalName = 'Icon' + name.split('-').map((s) => s.charAt(0).toUpperCase() + s.slice(1)).join('')
  const Icon = (TablerIcons as Record<string, unknown>)[pascalName] as React.FC<{ size?: number }> | undefined
  return Icon ? <Icon size={20} /> : null
}

export const Categories = () => {
  const queryClient = useQueryClient()
  const { data: categories = [] } = useQuery({ queryKey: ['categories'], queryFn: listCategories })
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState<CategoryResponse | null>(null)
  const [deleting, setDeleting] = useState<CategoryResponse | null>(null)

  const form = useForm({
    initialValues: { name: '', icon: '' },
    validate: {
      name: (v) => (v.trim() ? null : 'Name is required'),
      icon: (v) => (v.trim() ? null : 'Icon is required'),
    },
  })

  const openEdit = (cat: CategoryResponse) => {
    setEditing(cat)
    form.setValues({ name: cat.name, icon: cat.icon })
    setShowForm(true)
  }

  const openCreate = () => {
    setEditing(null)
    form.reset()
    setShowForm(true)
  }

  const handleSubmit = async (values: { name: string; icon: string }) => {
    try {
      if (editing) {
        await updateCategory(editing.id, values)
        notifications.show({ title: 'Saved', message: 'Category updated.', color: 'green' })
      } else {
        await createCategory(values)
        notifications.show({ title: 'Saved', message: 'Category created.', color: 'green' })
      }
      await queryClient.invalidateQueries({ queryKey: ['categories'] })
      setShowForm(false)
      form.reset()
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    }
  }

  const handleDelete = async () => {
    if (!deleting) return
    try {
      await deleteCategory(deleting.id)
      await queryClient.invalidateQueries({ queryKey: ['categories'] })
      notifications.show({ title: 'Deleted', message: 'Category removed.', color: 'green' })
    } catch (error) {
      notifications.show({ title: 'Error', message: getErrorMessage(error), color: 'red' })
    } finally {
      setDeleting(null)
    }
  }

  return (
    <Container size="sm" py="xl">
      <Group justify="space-between" mb="lg">
        <Title order={2}>Categories</Title>
        <Button onClick={openCreate}>+ Add Category</Button>
      </Group>

      {categories.length > 0 ? (
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th w={50}>Icon</Table.Th>
              <Table.Th>Name</Table.Th>
              <Table.Th>Actions</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {categories.map((cat) => (
              <Table.Tr key={cat.id}>
                <Table.Td>{resolveIcon(cat.icon) ?? cat.icon}</Table.Td>
                <Table.Td>{cat.name}</Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    <ActionIcon variant="subtle" onClick={() => openEdit(cat)} aria-label="Edit">
                      <IconEdit size={16} />
                    </ActionIcon>
                    <ActionIcon variant="subtle" color="red" onClick={() => setDeleting(cat)} aria-label="Delete">
                      <IconTrash size={16} />
                    </ActionIcon>
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      ) : (
        <Text c="dimmed">No categories yet. Add one to get started.</Text>
      )}

      <Modal opened={showForm} onClose={() => setShowForm(false)} title={editing ? 'Edit Category' : 'Add Category'}>
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <TextInput label="Name" placeholder="e.g. Groceries" mb="sm" {...form.getInputProps('name')} />
          <TextInput
            label="Icon"
            placeholder="e.g. shopping-cart"
            description="Tabler icon name — browse at tabler.io/icons"
            mb="sm"
            {...form.getInputProps('icon')}
          />
          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={() => setShowForm(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </Group>
        </form>
      </Modal>

      <Modal opened={!!deleting} onClose={() => setDeleting(null)} title="Delete Category">
        <Text>Are you sure you want to delete "{deleting?.name}"?</Text>
        <Group justify="flex-end" mt="md">
          <Button variant="default" onClick={() => setDeleting(null)}>Cancel</Button>
          <Button color="red" onClick={handleDelete}>Delete</Button>
        </Group>
      </Modal>
    </Container>
  )
}
