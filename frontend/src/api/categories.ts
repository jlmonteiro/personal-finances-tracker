/**
 * API client for categories resource.
 */
import { request } from './client'

export interface CategoryResponse {
  id: string
  name: string
  icon: string
  createdAt: string
  updatedAt: string
}

export interface CreateCategoryRequest {
  name: string
  icon: string
}

export interface UpdateCategoryRequest {
  name?: string
  icon?: string
}

export const listCategories = () => request<CategoryResponse[]>('/categories')

export const createCategory = (data: CreateCategoryRequest) =>
  request<CategoryResponse>('/categories', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const updateCategory = (id: string, data: UpdateCategoryRequest) =>
  request<CategoryResponse>(`/categories/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })

export const deleteCategory = (id: string) =>
  request<void>(`/categories/${id}`, { method: 'DELETE' })
