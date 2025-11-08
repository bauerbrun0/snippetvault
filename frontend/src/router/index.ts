import AdminView from '@/views/AdminView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SnippetsView from '@/views/SnippetsView.vue'
import TagsView from '@/views/TagsView.vue'
import { createRouter, createWebHistory } from 'vue-router'

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
    },
    {
      path: '/tags',
      name: 'tags',
      component: TagsView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
    },
  ],
})

export default router
