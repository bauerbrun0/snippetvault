<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Button from 'primevue/button'
import { useTitle } from '@vueuse/core'
import type { Tag } from '@/types/tag'
import { isResponseError } from 'up-fetch'
import { useToast } from 'primevue/usetoast'
import { errorSchema } from '@/utils/zod'
import { fetchTags, createTag } from '@/api/tag'
import TagCard from './components/TagCard.vue'
import Dialog from 'primevue/dialog'
import ColorPicker from 'primevue/colorpicker'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'

useTitle('Tags | SnippetVault')

const toast = useToast()

const tags = ref<Tag[]>([])
const loadingTags = ref(false)

const newTagDialogVisible = ref(false)
const newTagName = ref('')
const newTagColor = ref('ff0000')

onMounted(() => {
  loadTags()
})

async function loadTags() {
  try {
    loadingTags.value = true
    const tagsResponse = await fetchTags()
    tags.value = tagsResponse
  } catch (error) {
    let toastMessage = 'Failed to fetch tags.'
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
  loadingTags.value = false
}

async function onTagChange() {
  await loadTags()
}

async function onCreate() {
  try {
    await createTag(newTagName.value, `#${newTagColor.value}`)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Tag created.', life: 3000 })
    newTagDialogVisible.value = false
  } catch (error) {
    let toastMessage = 'Failed to create tag.'
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

  await loadTags()
}
</script>

<template>
  <div class="w-full flex justify-between items-center">
    <h2 class="text-3xl font-semibold my-6">Tags</h2>
    <Button label="New Tag" class="h-fit" @click="newTagDialogVisible = true" />
  </div>
  <div class="my-8">
    <div v-if="loadingTags" class="text-center">Loading snippets...</div>
    <TagCard
      v-else
      v-for="tag in tags"
      :key="tag.id"
      :tag="tag"
      :onAfterUpdate="onTagChange"
      :onAfterDelete="onTagChange"
    />
  </div>

  <Dialog
    v-model:visible="newTagDialogVisible"
    modal
    header="Create new tag"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel variant="in">
        <InputText v-model="newTagName" inputId="name" variant="filled" fluid :feedback="false" />
        <label for="name">Name</label>
      </FloatLabel>
    </div>
    <div class="flex items-center gap-4 my-8">
      <label for="color">Color</label>
      <ColorPicker v-model="newTagColor" inputId="color" format="hex" class="" />
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        severity="secondary"
        @click="newTagDialogVisible = false"
      ></Button>
      <Button type="button" label="Create" @click="onCreate"></Button>
    </div>
  </Dialog>
</template>
