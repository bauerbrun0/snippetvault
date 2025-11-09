<script setup lang="ts">
import { ref, computed } from 'vue'
import Avatar from 'primevue/avatar'
import Menubar from 'primevue/menubar'
import Menu from 'primevue/menu'
import type { MenuItem } from 'primevue/menuitem'
import SnippetVaultLogo from './SnippetVaultLogo.vue'
import { userStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userMenuElement = ref()

const menuItems = computed<MenuItem[]>(() => {
  if (userStore.user !== null) {
    return [
      {
        label: 'Snippets',
        route: '/snippets',
      },
      {
        label: 'Tags',
        route: '/tags',
      },
    ]
  }
  return []
})

const userMenuItems = computed<MenuItem[]>(() => {
  const items: MenuItem[] = [
    {
      label: 'Profile',
      route: '/profile',
    },
  ]

  if (userStore.user?.isAdmin) {
    items.push({
      label: 'Admin',
      route: '/admin',
    })
  }

  items.push({
    label: 'Logout',
    command: () => {
      userStore.logout()
      router.push('/login')
    },
  })

  return items
})

const toggle = (event: Event) => {
  userMenuElement.value.toggle(event)
}
</script>

<template>
  <div class="card min-h-[50px]">
    <Menubar :model="menuItems" class="">
      <template #start>
        <SnippetVaultLogo />
      </template>
      <template #item="{ item, props }">
        <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
          <a v-ripple :href="href" v-bind="props.action" @click="navigate">
            <span :class="item.icon" />
            <span>{{ item.label }}</span>
          </a>
        </router-link>
        <a v-else v-ripple :href="item.url" :target="item.target" v-bind="props.action">
          <span :class="item.icon" />
          <span>{{ item.label }}</span>
        </a>
      </template>
      <template #end>
        <div v-if="userStore.user !== null">
          <div class="flex items-center gap-2">
            <button
              type="button"
              @click="toggle"
              aria-haspopup="true"
              aria-controls="overlay_menu"
              class="cursor-pointer"
            >
              <Avatar
                :label="userStore.user.username.charAt(0).toUpperCase()"
                class="mr-2"
                size="normal"
                shape="circle"
              />
            </button>
          </div>
          <Menu ref="userMenuElement" id="overlay_menu" :model="userMenuItems" :popup="true">
            <template #item="{ item, props }">
              <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
                <a v-ripple :href="href" v-bind="props.action" @click="navigate">
                  <span :class="item.icon" />
                  <span class="ml-2">{{ item.label }}</span>
                </a>
              </router-link>
              <a v-else v-ripple :href="item.url" :target="item.target" v-bind="props.action">
                <span :class="item.icon" />
                <span class="ml-2">{{ item.label }}</span>
              </a>
            </template>
          </Menu>
        </div>
      </template>
    </Menubar>
  </div>
</template>
