/**
 * API client for expenses resource.
 */
import { request } from './client'

export interface ExpenseResponse {
  id: string
  quarterId: string
  quarterNumber: number
  payee: { id: string; name: string }
  category: { id: string; name: string; icon: string }
  title: string
  description: string | null
  expectedValue: number
  actualValue: number | null
  dueDate: string
  paymentDate: string | null
  status: 'PENDING' | 'PAID' | 'OVERDUE'
  isOverride: boolean
  bankAccountId: string | null
  createdAt: string
}

export interface CreateExpenseRequest {
  payeeId: string
  categoryId: string
  title: string
  description?: string
  expectedValue: number
  dueDate: string
  bankAccountId: string
}

export interface UpdateExpenseRequest {
  title?: string
  description?: string
  expectedValue?: number
  actualValue?: number
  dueDate?: string
  paymentDate?: string
  clearPayment?: boolean
}

export const listExpenses = (monthId: string) =>
  request<ExpenseResponse[]>(`/financial-months/${monthId}/expenses`)

export const createExpense = (monthId: string, data: CreateExpenseRequest) =>
  request<ExpenseResponse>(`/financial-months/${monthId}/expenses`, {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const updateExpense = (id: string, data: UpdateExpenseRequest) =>
  request<ExpenseResponse>(`/expenses/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })

export const deleteExpense = (id: string) =>
  request<void>(`/expenses/${id}`, { method: 'DELETE' })
