<script setup lang="ts">
import { ref } from 'vue'
import Button from 'primevue/button'

const count = ref(0)

function inc() {
  count.value++
}
</script>

<template>
  <h1 class="text-3xl text-primary-500 font-bold underline">Hello world!</h1>
  <p>
    {{ count }}
  </p>
  <Button label="increment" @click="inc()" />
</template>

<style scoped></style>
