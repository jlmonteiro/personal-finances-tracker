/**
 * API client for income source endpoints.
 */
import { request } from './client'

export interface Money {
  value: string
  currency: string
}

export interface IncomeSourceResponse {
  id: string
  name: string
  description: string | null
  amount: Money
  frequency: string
  paymentDateType: string
  paymentDateRule: string
  startDate: string
  endDate: string | null
  isActive: boolean
  createdAt: string
}

export interface CreateIncomeSourceRequest {
  name: string
  description?: string
  amount: Money
  frequency: string
  paymentDateType: string
  paymentDateRule: string
  startDate: string
  endDate?: string | null
}

export interface UpdateIncomeSourceRequest {
  name?: string
  description?: string
  amount?: Money
  frequency?: string
  paymentDateType?: string
  paymentDateRule?: string
  startDate?: string
  endDate?: string | null
  isActive?: boolean
}

export interface PaginatedResponse<T> {
  data: T[]
  pagination: {
    page: number
    size: number
    totalElements: number
    totalPages: number
  }
}

/**
 * Fetches a paginated list of income sources.
 */
export const listIncomeSources = async (page = 1, size = 20): Promise<PaginatedResponse<IncomeSourceResponse>> => {
  return request<PaginatedResponse<IncomeSourceResponse>>(`/income-sources?page=${page}&size=${size}`)
}

/**
 * Creates a new income source.
 */
export const createIncomeSource = async (data: CreateIncomeSourceRequest): Promise<IncomeSourceResponse> => {
  return request<IncomeSourceResponse>('/income-sources', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

/**
 * Updates an existing income source.
 */
export const updateIncomeSource = async (id: string, data: UpdateIncomeSourceRequest): Promise<IncomeSourceResponse> => {
  return request<IncomeSourceResponse>(`/income-sources/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })
}

/**
 * Deletes an income source.
 */
export const deleteIncomeSource = async (id: string): Promise<void> => {
  return request<void>(`/income-sources/${id}`, {
    method: 'DELETE',
  })
}
