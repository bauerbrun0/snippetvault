import { reactive } from 'vue'
import type { User } from '@/types/user'
import { useToastService } from '@/utils/toast'
import { fetchAuthenticatedUser } from '@/api/user'
import { isResponseError } from 'up-fetch'
import { errorSchema } from '@/utils/zod'
import router from '@/router'

const { showToast } = useToastService()

export type UserStore = {
  user: User | null
  token: string | null
  loadingUser: boolean

  setUser: (user: User) => void
  isLoggedIn: () => boolean
  isAdmin: () => boolean
  fetchUser: (targetPath: string) => Promise<void>
  setLoadingUser: (loading: boolean) => void
  logout: () => void
}

export const userStore: UserStore = reactive<UserStore>({
  user: null,
  token: localStorage.getItem('token') || null,
  loadingUser: true,
  isLoggedIn: () => userStore.user !== null,
  isAdmin: () => userStore.user?.isAdmin ?? false,

  setUser(user: User) {
    this.user = user
  },
  fetchUser: async function (targetPath: string) {
    if (!this.token) {
      this.setLoadingUser(false)
      return
    }

    try {
      const user = await fetchAuthenticatedUser()
      this.setUser(user)
      this.setLoadingUser(false)
      if (targetPath === '/login') targetPath = '/snippets'
      router.push({ path: targetPath })
    } catch (error) {
      let toastMessage = 'Failed to fetch user data.'
      if (!isResponseError(error)) {
        showToast('error', 'Error', toastMessage, 2000)
        this.setLoadingUser(false)
        return
      }

      const errorResponse = errorSchema.safeParse(error.data)
      if (errorResponse.success) {
        toastMessage = errorResponse.data.error
      }

      showToast('error', 'Error', toastMessage, 2000)
      this.setLoadingUser(false)
    }
  },
  setLoadingUser(loading: boolean) {
    this.loadingUser = loading
  },
  logout() {
    this.user = null
    this.token = null
    localStorage.removeItem('token')
    showToast('info', 'Logged Out', 'You have been logged out.', 2000)
  },
})
