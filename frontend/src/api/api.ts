import { up } from 'up-fetch'
import { userStore } from '@/stores/user'

export const upfetch = up(fetch, () => ({
  baseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: { Authorization: userStore.token ? `Bearer ${userStore.token}` : undefined },
}))
