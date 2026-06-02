/**
 * Base HTTP client for API calls.
 * Prepends base URL and handles JSON serialization.
 */

const BASE_URL = '/api/v1'

interface ApiError {
  type: string
  title: string
  status: number
  detail: string
  errors?: { field: string; message: string }[]
}

/**
 * Extracts a human-readable error message from an API error.
 */
export const getErrorMessage = (error: unknown): string => {
  const apiError = error as ApiError
  if (apiError?.errors?.length) {
    return apiError.errors.map((e) => `${e.field}: ${e.message}`).join(', ')
  }
  if (apiError?.detail) {
    return apiError.detail
  }
  return 'An unexpected error occurred'
}

/**
 * Performs an HTTP request to the API.
 * Throws an ApiError on non-2xx responses.
 */
export const request = async <T>(path: string, options?: RequestInit): Promise<T> => {
  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  })

  if (!res.ok) {
    let error: ApiError
    try {
      error = await res.json()
    } catch {
      error = {
        type: 'about:blank',
        title: `HTTP ${res.status}`,
        status: res.status,
        detail: res.statusText || 'Request failed',
      }
    }
    throw error
  }

  if (res.status === 204) {
    return undefined as T
  }

  return res.json()
}

export type { ApiError }
