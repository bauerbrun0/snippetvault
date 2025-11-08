<script setup lang="ts">
import HeaderComponent from '@/components/header/HeaderComponent.vue'
import Toast from 'primevue/toast'
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <HeaderComponent />
    <main class="flex-1 p-2 container mx-auto">
      <slot />
    </main>
  </div>
  <Toast />
</template>
