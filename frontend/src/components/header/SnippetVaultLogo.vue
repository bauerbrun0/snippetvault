<script setup lang="ts">
import CodeIcon from '@/icons/CodeIcon.vue'
</script>

<template>
  <router-link to="/snippets">
    <div class="flex items-center gap-3 mr-4">
      <CodeIcon classes="stroke-primary-500 h-7 w-7" />
      <h1 class="text-lg font-semibold">SnippetVault</h1>
    </div>
  </router-link>
</template>
