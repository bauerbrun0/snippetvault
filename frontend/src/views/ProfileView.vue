<script setup lang="ts">
import { useTitle } from '@vueuse/core'
import Card from 'primevue/card'
import { userStore } from '@/stores/user'

useTitle('Profile | SnippetVault')
</script>

<template>
  <h2 class="text-3xl font-semibold my-6">Profile Page</h2>
  <Card>
    <template #title> Your Profile </template>
    <template #content>
      <div class="flex flex-col gap-4">
        <div><strong>Username:</strong> {{ userStore.user?.username }}</div>
        <div><strong>ID:</strong> {{ userStore.user?.id }}</div>
        <div><strong>Created:</strong> {{ userStore.user?.created.toLocaleString() }}</div>
        <div>
          <strong>Role: </strong>
          <span v-if="userStore.user?.isAdmin">Administrator</span>
          <span v-else>User</span>
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped></style>
