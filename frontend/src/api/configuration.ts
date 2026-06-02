/**
 * API client for configuration endpoints.
 */
import { request } from './client'

export interface ConfigurationResponse {
  id: string
  currency: string
  monthStartDay: number
  createdAt: string
}

export interface CreateConfigurationRequest {
  currency: string
  monthStartDay: number
}

export interface UpdateConfigurationRequest {
  currency?: string
  monthStartDay?: number
}

/**
 * Fetches the current application configuration.
 * Returns null if not yet configured (404).
 */
export const getConfiguration = async (): Promise<ConfigurationResponse | null> => {
  try {
    return await request<ConfigurationResponse>('/configuration')
  } catch (error: unknown) {
    if ((error as { status?: number }).status === 404) {
      return null
    }
    throw error
  }
}

/**
 * Creates the initial application configuration.
 */
export const createConfiguration = async (data: CreateConfigurationRequest): Promise<ConfigurationResponse> => {
  return request<ConfigurationResponse>('/configuration', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

/**
 * Updates the existing configuration.
 */
export const updateConfiguration = async (data: UpdateConfigurationRequest): Promise<ConfigurationResponse> => {
  return request<ConfigurationResponse>('/configuration', {
    method: 'PATCH',
    body: JSON.stringify(data),
  })
}
