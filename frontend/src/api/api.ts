import { isResponseError, up } from 'up-fetch'
import { userStore } from '@/stores/user'
import { errorSchema } from '@/utils/zod'

export const upfetch = up(fetch, () => ({
  baseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: { Authorization: userStore.token ? `Bearer ${userStore.token}` : undefined },
  onError: (error) => {
    if (!isResponseError(error)) {
      throw error
    }
    if (error.status === 403) {
      const res = errorSchema.safeParse(error.data)
      if (res.success && res.data.error === 'Token expired') {
        userStore.logout()
      }
    }
  },
}))
