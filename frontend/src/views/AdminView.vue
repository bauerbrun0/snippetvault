<script setup lang="ts">
import Button from 'primevue/button'
import Card from 'primevue/card'
import Badge from 'primevue/badge'
import { ref, onMounted } from 'vue'
import type { User } from '@/types/user'
import { fetchUsers, deleteUser } from '@/api/user'
import { isResponseError } from 'up-fetch'
import { errorSchema } from '@/utils/zod'
import { useToast } from 'primevue/usetoast'
import { userStore } from '@/stores/user'
import { useTitle } from '@vueuse/core'

useTitle('Admin | SnippetVault')

const toast = useToast()

const loading = ref(false)
const users = ref<User[]>([])

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const usersResponse = await fetchUsers()
    users.value = usersResponse
  } catch (error) {
    let toastMessage = 'Failed to fetch users.'
    if (!isResponseError(error)) {
      toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
      return
    }

    const errorResponse = errorSchema.safeParse(error.data)
    if (errorResponse.success) {
      toastMessage = errorResponse.data.error
    }

    toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
  }
  loading.value = false
}

async function handleDeleteUserClick(userId: number) {
  try {
    const user = await deleteUser(userId)
    users.value = users.value.filter((u) => u.id !== user.id)
    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: `User with ${user.username} deleted successfully.`,
      life: 3000,
    })
  } catch (error) {
    let toastMessage = 'Failed to delete user.'
    if (!isResponseError(error)) {
      toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
      return
    }

    const errorResponse = errorSchema.safeParse(error.data)
    if (errorResponse.success) {
      toastMessage = errorResponse.data.error
    }

    toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
  }
}
</script>

<template>
  <h2 class="text-3xl font-semibold my-6">Admin Page</h2>
  <div>
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-xl font-semibold">User Management</h3>
      <router-link to="/admin/create-user">
        <Button label="Add New User" icon="pi pi-plus" class="p-button-sm" />
      </router-link>
    </div>
    <div v-if="loading" class="text-center">Loading users...</div>
    <div v-else>
      <Card v-for="user in users" :key="user.id" class="mb-4">
        <template #title>
          <div class="flex items-center gap-3">
            <span>{{ user.username }}</span>
            <Badge v-if="user.isAdmin" value="Admin" severity="warn" class="w-fit" />
            <Badge value="User" severity="success" class="w-fit" />
          </div>
        </template>
        <template #content>
          <div class="flex flex-col justify-between gap-2 my-2">
            <div class="text-sm">Created: {{ new Date(user.created).toLocaleString() }}</div>
            <div class="text-sm">ID: {{ user.id }}</div>
            <div class="flex gap-2">
              <Button
                label="Delete"
                icon="pi pi-trash"
                class="p-button-sm p-button-danger"
                :disabled="user.id === userStore.user?.id"
                @click="handleDeleteUserClick(user.id)"
              />
            </div>
          </div>
        </template>
      </Card>
    </div>
  </div>
</template>

<style scoped></style>
