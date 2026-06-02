/**
 * API client for income entries (per-month income).
 */
import { request } from './client'

export interface IncomeEntryResponse {
  id: string
  financialMonthId: string
  incomeSourceId: string | null
  name: string
  amount: number
  isAdhoc: boolean
  createdAt: string
}

export interface CreateIncomeEntryRequest {
  name: string
  amount: number
}

export interface UpdateIncomeEntryRequest {
  name?: string
  amount?: number
}

export const listIncomeEntries = (monthId: string) =>
  request<IncomeEntryResponse[]>(`/financial-months/${monthId}/income-entries`)

export const createIncomeEntry = (monthId: string, data: CreateIncomeEntryRequest) =>
  request<IncomeEntryResponse>(`/financial-months/${monthId}/income-entries`, {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const updateIncomeEntry = (id: string, data: UpdateIncomeEntryRequest) =>
  request<IncomeEntryResponse>(`/income-entries/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })

export const deleteIncomeEntry = (id: string) =>
  request<void>(`/income-entries/${id}`, { method: 'DELETE' })
