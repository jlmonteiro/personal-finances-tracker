/**
 * API client for financial months resource.
 */
import { request } from './client'

export interface FinancialMonthResponse {
  id: string
  startDate: string
  endDate: string
  createdAt: string
}

export interface QuarterResponse {
  id: string
  quarterNumber: number
  startDate: string
  endDate: string
}

export interface CreateFinancialMonthRequest {
  year: number
  month: number
}

export const listFinancialMonths = () =>
  request<FinancialMonthResponse[]>('/financial-months')

export const getFinancialMonth = (id: string) =>
  request<FinancialMonthResponse>(`/financial-months/${id}`)

export const getQuarters = (monthId: string) =>
  request<QuarterResponse[]>(`/financial-months/${monthId}/quarters`)

export const createFinancialMonth = (data: CreateFinancialMonthRequest) =>
  request<FinancialMonthResponse>('/financial-months', {
    method: 'POST',
    body: JSON.stringify(data),
  })
