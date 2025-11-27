<script setup lang="ts">
import { createEditor } from 'prism-code-editor'
import 'prism-code-editor/prism/languages'
import 'prism-code-editor/layout.css'
import 'prism-code-editor/themes/github-dark.css'
import 'prism-code-editor/search.css'
import 'prism-code-editor/copy-button.css'
import 'prism-code-editor/guides.css'
import type { Language } from '@/types/language'
import type { PrismEditor } from 'prism-code-editor'
import { searchWidget, highlightSelectionMatches } from 'prism-code-editor/search'
import { defaultCommands, editHistory } from 'prism-code-editor/commands'
import { cursorPosition } from 'prism-code-editor/cursor'
import { copyButton } from 'prism-code-editor/copy-button'
import { matchTags } from 'prism-code-editor/match-tags'
import { highlightBracketPairs } from 'prism-code-editor/highlight-brackets'
import { indentGuides } from 'prism-code-editor/guides'
import { onMounted, watch, ref } from 'vue'

const props = defineProps<{
  fileId: number
}>()
const content = defineModel<string>('content')
const language = defineModel<Language>('language')

let editor: PrismEditor | null = null
const editorEl = ref<HTMLElement | null>(null)

onMounted(() => {
  editor = createEditor('#editor-' + props.fileId, {
    language: language.value?.name,
    value: content.value,
    lineNumbers: true,
    onUpdate: (newValue: string) => {
      content.value = newValue
    },
  })
  editor.addExtensions(
    highlightSelectionMatches(),
    searchWidget(),
    defaultCommands(),
    copyButton(),
    matchTags(),
    highlightBracketPairs(),
    cursorPosition(),
    editHistory(),
    indentGuides(),
  )
})

watch(language, (newLang) => {
  if (editor) {
    editor.setOptions({
      value: content.value,
      language: newLang?.name,
    })
  }
})
</script>

<template>
  <div class="overflow-auto max-h-[800px]">
    <div :id="'editor-' + fileId" ref="editorEl"></div>
  </div>
</template>
