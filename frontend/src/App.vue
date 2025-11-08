<script setup lang="ts">
import AppLayout from './layouts/AppLayout.vue'
import { userStore } from '@/stores/user'
</script>

<template>
  <AppLayout>
    <div v-if="userStore.loadingUser">Loading user...</div>
    <router-view v-else />
  </AppLayout>
</template>
