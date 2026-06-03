/**
 * API client for payees resource.
 */
import { request } from './client'
import { CategoryResponse } from './categories'

export interface PayeeResponse {
  id: string
  name: string
  categories: CategoryResponse[]
  createdAt: string
  updatedAt: string
}

export interface CreatePayeeRequest {
  name: string
  categoryIds: string[]
}

export interface UpdatePayeeRequest {
  name?: string
  categoryIds?: string[]
}

export const listPayees = () => request<PayeeResponse[]>('/payees')

export const createPayee = (data: CreatePayeeRequest) =>
  request<PayeeResponse>('/payees', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const updatePayee = (id: string, data: UpdatePayeeRequest) =>
  request<PayeeResponse>(`/payees/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })

export const deletePayee = (id: string) =>
  request<void>(`/payees/${id}`, { method: 'DELETE' })
