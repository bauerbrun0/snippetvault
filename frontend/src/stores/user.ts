import { reactive } from 'vue'
import type { User } from '@/types/user'

const defaultUser: User = {
  username: 'john',
  created: new Date('2022-01-01T00:00:00Z'),
  isAdmin: false,
}

export type UserStore = {
  user: User | null
  setUser: (user: User) => void
  isLoggedIn: () => boolean
  isAdmin: () => boolean
}

export const userStore: UserStore = reactive({
  user: defaultUser,
  isLoggedIn: () => userStore.user !== null,
  isAdmin: () => userStore.user?.isAdmin ?? false,
  setUser(user: User) {
    this.user = user
  },
})
