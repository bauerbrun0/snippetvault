<script setup lang="ts">
import { ref } from 'vue'
import Button from 'primevue/button'
import { useTitle } from '@vueuse/core'

useTitle('Profile | SnippetVault')

const count = ref(0)

function inc() {
  count.value++
}
</script>

<template>
  <h2 class="text-xl mb-4">Profile Page</h2>
  <p>
    {{ count }}
  </p>
  <Button label="increment" @click="inc()" />
</template>

<style scoped></style>
