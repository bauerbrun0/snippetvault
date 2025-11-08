<script setup lang="ts">
import { ref } from 'vue'
import Avatar from 'primevue/avatar'
import Menubar from 'primevue/menubar'
import Menu from 'primevue/menu'
import type { MenuItem } from 'primevue/menuitem'
import SnippetVaultLogo from './SnippetVaultLogo.vue'

const userMenuElement = ref()

const menuItems = ref<MenuItem[]>([
  {
    label: 'Snippets',
    route: '/snippets',
  },
  {
    label: 'Tags',
    route: '/tags',
  },
])

const userMenuItems = ref<MenuItem[]>([
  {
    label: 'Profile',
    route: '/profile',
  },
  {
    label: 'Admin',
    route: '/admin',
  },
  {
    label: 'Logout',
    command: () => {
      console.log('Logging out...')
    },
  },
])

const toggle = (event: Event) => {
  userMenuElement.value.toggle(event)
}
</script>

<template>
  <div class="card">
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
        <div class="flex items-center gap-2">
          <button
            type="button"
            @click="toggle"
            aria-haspopup="true"
            aria-controls="overlay_menu"
            class="cursor-pointer"
          >
            <Avatar label="P" class="mr-2" size="normal" shape="circle" />
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
      </template>
    </Menubar>
  </div>
</template>
