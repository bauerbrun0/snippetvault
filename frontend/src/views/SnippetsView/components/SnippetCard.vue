<script setup lang="ts">
import type { DetailedSnippet } from '@/types/snippetSearchResult'
import type { Tag } from '@/types/tag'
import type { Language } from '@/types/language'
import Card from 'primevue/card'
import { computed } from 'vue'
import { getLuminance } from 'color2k'

const props = defineProps<{
  snippet: DetailedSnippet
  tags: Tag[]
  languages: Language[]
}>()

const tagMap = computed(() => {
  const map: Record<number, Tag> = {}
  for (const tag of props.tags) {
    map[tag.id] = tag
  }
  return map
})

const languageMap = computed(() => {
  const map: Record<number, Language> = {}
  for (const language of props.languages) {
    map[language.id] = language
  }
  return map
})
</script>

<template>
  <router-link :to="`/snippets/${snippet.id}`" class="no-underline">
    <Card class="mb-3 hover:bg-surface-800!">
      <template #title>
        <div class="flex gap-4 items-center">
          <h2>{{ snippet.title }}</h2>
          <div class="flex gap-2 items-center">
            <div
              v-for="tagId in snippet.tagIds"
              :key="tagId"
              class="text-sm rounded-md px-2 py-1"
              :style="{
                backgroundColor: tagMap[tagId]?.color,
                color: getLuminance(tagMap[tagId]?.color as string) > 0.5 ? 'black' : 'white',
              }"
            >
              {{ tagMap[tagId]?.name }}
            </div>
          </div>
        </div>
      </template>
      <template #content>
        <div class="flex flex-col gap-1">
          <span class="text-sm text-gray-400">
            Last update: {{ snippet.updated.toLocaleString() }}
          </span>
          <span v-if="snippet.fileCount > 0">
            {{ snippet.fileCount }} file{{ snippet.fileCount !== 1 ? 's' : '' }} &middot;
            {{
              snippet.languageIds
                .slice(0, 3)
                .map((id) => languageMap[id]?.name)
                .join(', ')
            }}
            <span v-if="snippet.languageIds.length > 3">
              &middot; and {{ snippet.languageIds.length - 3 }} more
            </span>
          </span>
          <span>
            {{ snippet.description }}
          </span>
        </div>
      </template>
    </Card>
  </router-link>
</template>
