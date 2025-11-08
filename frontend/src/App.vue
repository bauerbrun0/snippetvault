<script setup lang="ts">
import { ref } from 'vue'
import Button from 'primevue/button'
import AppLayout from './layouts/AppLayout.vue'

const count = ref(0)

function inc() {
  count.value++
}
</script>

<template>
  <AppLayout>
    <h2 class="text-xl mb-4">Home Page</h2>
    <p>
      {{ count }}
    </p>
    <Button label="increment" @click="inc()" />
  </AppLayout>
</template>

<style scoped></style>
