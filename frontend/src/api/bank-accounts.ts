/**
 * API client for bank accounts resource.
 */
import { request } from './client'

export interface BankAccountResponse {
  id: string
  name: string
  description: string | null
  hasLogo: boolean
  createdAt: string
}

export interface CreateBankAccountRequest {
  name: string
  description?: string
}

export interface UpdateBankAccountRequest {
  name?: string
  description?: string
}

export const listBankAccounts = () => request<BankAccountResponse[]>('/bank-accounts')

export const createBankAccount = (data: CreateBankAccountRequest) =>
  request<BankAccountResponse>('/bank-accounts', { method: 'POST', body: JSON.stringify(data) })

export const updateBankAccount = (id: string, data: UpdateBankAccountRequest) =>
  request<BankAccountResponse>(`/bank-accounts/${id}`, { method: 'PATCH', body: JSON.stringify(data) })

export const deleteBankAccount = (id: string) =>
  request<void>(`/bank-accounts/${id}`, { method: 'DELETE' })

export const getBankAccountLogoUrl = (id: string) => `/api/v1/bank-accounts/${id}/logo`
