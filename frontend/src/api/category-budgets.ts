/**
 * API client for category budgets.
 */
import { request } from './client'

export interface CategoryBudgetResponse {
  id: string
  quarterId: string
  categoryId: string
  categoryName: string
  categoryIcon: string
  amount: number
}

export const listCategoryBudgets = (quarterId: string) =>
  request<CategoryBudgetResponse[]>(`/quarters/${quarterId}/budgets`)

export const upsertCategoryBudget = (quarterId: string, categoryId: string, amount: number) =>
  request<CategoryBudgetResponse>(`/quarters/${quarterId}/budgets/${categoryId}`, {
    method: 'PUT',
    body: JSON.stringify({ amount }),
  })

export const deleteCategoryBudget = (quarterId: string, categoryId: string) =>
  request<void>(`/quarters/${quarterId}/budgets/${categoryId}`, { method: 'DELETE' })
