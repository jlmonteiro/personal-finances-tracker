/**
 * Hook for fetching and managing application configuration.
 */
import { useQuery } from '@tanstack/react-query'
import { getConfiguration } from '../api/configuration'

/**
 * Fetches the application configuration.
 * Returns null if not yet configured (triggers setup wizard redirect).
 */
export const useConfiguration = () => {
  return useQuery({
    queryKey: ['configuration'],
    queryFn: getConfiguration,
    retry: 3,
    retryDelay: (attempt) => Math.min(1000 * 2 ** attempt, 5000),
  })
}
