import AdminView from '@/views/AdminView.vue'
import LoginView from '@/views/LoginView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SnippetsView from '@/views/SnippetsView.vue'
import TagsView from '@/views/TagsView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { userStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/snippets',
    },
    {
      path: '/snippets',
      name: 'snippets',
      component: SnippetsView,
      meta: { requiresAuth: true },
    },
    {
      path: '/tags',
      name: 'tags',
      component: TagsView,
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true },
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: {
        requiresAuth: true,
        requiresAdmin: true,
      },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const isLoggedIn = userStore.isLoggedIn()
  const isAdmin = userStore.isAdmin()

  if (to.meta.requiresAuth && !isLoggedIn) return next({ name: 'login' })
  if (to.meta.requiresAdmin && !isAdmin) return next({ name: 'snippets' })
  if (to.name === 'login' && isLoggedIn) return next({ name: 'snippets' })
  next()
})

export default router
