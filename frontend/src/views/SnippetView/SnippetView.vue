<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Dialog from 'primevue/dialog'
import Textarea from 'primevue/textarea'
import InputText from 'primevue/inputtext'
import MultiSelect from 'primevue/multiselect'
import Select from 'primevue/select'
import { useTitle } from '@vueuse/core'
import type { Tag } from '@/types/tag'
import type { Language } from '@/types/language'
import type { Snippet } from '@/types/snippet'
import { isResponseError } from 'up-fetch'
import { useToast } from 'primevue/usetoast'
import { errorSchema } from '@/utils/zod'
import { fetchTags } from '@/api/tag'
import { fetchLanguages } from '@/api/language'
import {
  fetchSnippetById,
  deleteSnippet,
  updateSnippet,
  fetchTagsForSnippet,
  addTagToSnippet,
  removeTagFromSnippet,
  fetchFilesForSnippet,
  createFileForSnippet,
} from '@/api/snippet'
import { useRoute, useRouter } from 'vue-router'
import type { File } from '@/types/file'
import FileCard from './components/FileCard.vue'

useTitle('Snippet | SnippetVault')

const toast = useToast()
const route = useRoute()
const router = useRouter()
const snippetId = parseInt(route.params.id as string)

const loadingSnippet = ref(true)
const snippet = ref<Snippet | null>(null)
const newSnippet = ref<Snippet>(null as unknown as Snippet)
const snippetChanged = computed(() => {
  return (
    snippet.value?.title !== newSnippet.value.title ||
    snippet.value?.description !== newSnippet.value.description
  )
})
const tagsChanged = computed(() => {
  return tagsToAdd.value.length > 0 || tagsToRemove.value.length > 0
})

const tagsOnSnippet = ref<Tag[]>([])
const newTagsOnSnippet = ref<Tag[]>([])
const tagsToAdd = computed(() => {
  return newTagsOnSnippet.value.filter(
    (tag) => !tagsOnSnippet.value.some((existingTag) => existingTag.id === tag.id),
  )
})
const tagsToRemove = computed(() => {
  return tagsOnSnippet.value.filter(
    (tag) => !newTagsOnSnippet.value.some((existingTag) => existingTag.id === tag.id),
  )
})

const hasChanges = computed(() => {
  return snippetChanged.value || tagsChanged.value
})

const editTitleDialogVisible = ref(false)
const deleteSnippetDialogVisible = ref(false)
const addFileDialogVisible = ref(false)

const newFileFilename = ref('')
const newFileLanguage = ref<Language>(null as unknown as Language)

const tags = ref<Tag[]>([])
const languages = ref<Language[]>([])

const files = ref<File[]>([])

onMounted(() => {
  loadData()
})

async function loadData() {
  await Promise.all([loadTags(), loadLanguages()])

  await Promise.all([loadSnippet(), loadTagsForSnippet(), loadFilesForSnippet()])
}

async function loadTags() {
  try {
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
}

async function loadLanguages() {
  try {
    const languagesResponse = await fetchLanguages()
    languages.value = languagesResponse
    newFileLanguage.value = languagesResponse[0]
      ? languagesResponse[0]
      : (null as unknown as Language)
  } catch (error) {
    let toastMessage = 'Failed to fetch languages.'
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

async function loadTagsForSnippet() {
  try {
    const tagsResponse = await fetchTagsForSnippet(snippetId)
    tagsOnSnippet.value = tagsResponse
    newTagsOnSnippet.value = [...tagsResponse]
  } catch (error) {
    let toastMessage = 'Failed to fetch tags for snippet.'
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

async function loadSnippet() {
  try {
    const snippetResponse = await fetchSnippetById(snippetId)
    snippet.value = snippetResponse
    newSnippet.value = { ...snippetResponse }
    loadingSnippet.value = false
    useTitle(`${snippetResponse.title} | SnippetVault`)
  } catch (error) {
    let toastMessage = 'Failed to fetch snippet.'
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

async function loadFilesForSnippet() {
  try {
    const filesResponse = await fetchFilesForSnippet(snippetId)
    files.value = filesResponse
  } catch (error) {
    let toastMessage = 'Failed to fetch files for snippet.'
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

function onCancelEditTitle() {
  newSnippet.value.title = snippet.value?.title || ''
  editTitleDialogVisible.value = false
}

function onUndoChanges() {
  newSnippet.value = { ...snippet.value } as Snippet
  newTagsOnSnippet.value = [...tagsOnSnippet.value]
}

async function onDeleteSnippet() {
  try {
    await deleteSnippet(snippetId)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Snippet deleted.', life: 3000 })
    deleteSnippetDialogVisible.value = false
    router.push('/snippets')
  } catch (error) {
    let toastMessage = 'Failed to delete snippet.'
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

async function onSaveChanges() {
  if (snippetChanged.value) {
    try {
      const snippetResponse = await updateSnippet(
        snippetId,
        newSnippet.value.title,
        newSnippet.value.description,
      )
      toast.add({ severity: 'success', summary: 'Success', detail: 'Snippet updated.', life: 3000 })
      snippet.value = snippetResponse
      newSnippet.value = { ...snippetResponse }
      useTitle(`${snippetResponse.title} | SnippetVault`)
    } catch (error) {
      let toastMessage = 'Failed to update snippet.'
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
  if (tagsChanged.value) {
    for (const tag of tagsToAdd.value) {
      try {
        await addTagToSnippet(snippetId, tag.id)
        tagsOnSnippet.value.push(tag)
      } catch (error) {
        let toastMessage = `Failed to add tag "${tag.name}" to snippet.`
        if (!isResponseError(error)) {
          toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
          continue
        }

        const errorResponse = errorSchema.safeParse(error.data)
        if (errorResponse.success) {
          toastMessage = errorResponse.data.error
        }

        toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
      }
    }
    for (const tag of tagsToRemove.value) {
      try {
        await removeTagFromSnippet(snippetId, tag.id)
        tagsOnSnippet.value = tagsOnSnippet.value.filter((t) => t.id !== tag.id)
      } catch (error) {
        let toastMessage = `Failed to remove tag "${tag.name}" from snippet.`
        if (!isResponseError(error)) {
          toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
          continue
        }

        const errorResponse = errorSchema.safeParse(error.data)
        if (errorResponse.success) {
          toastMessage = errorResponse.data.error
        }

        toast.add({ severity: 'error', summary: 'Error', detail: toastMessage, life: 3000 })
      }
    }
    newTagsOnSnippet.value = [...tagsOnSnippet.value]
    toast.add({ severity: 'success', summary: 'Success', detail: 'Tags updated.', life: 3000 })
  }
}

async function onAddFile() {
  try {
    await createFileForSnippet(
      snippetId,
      newFileFilename.value,
      'print("Hello, World!")',
      newFileLanguage.value.id,
    )
    toast.add({ severity: 'success', summary: 'Success', detail: 'File added.', life: 3000 })
    newFileFilename.value = ''
    newFileLanguage.value = languages.value[0] ? languages.value[0] : (null as unknown as Language)
    addFileDialogVisible.value = false
    loadFilesForSnippet()
  } catch (error) {
    let toastMessage = 'Failed to add file to snippet.'
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

async function onAfterUpdateFile() {
  await loadFilesForSnippet()
}
</script>

<template>
  <div v-if="loadingSnippet" class="w-full flex justify-center items-center h-64">
    Loading snippet with id {{ snippetId }}...
  </div>
  <div v-else>
    <div class="w-full flex justify-between items-center">
      <div class="flex items-center gap-3">
        <h2 class="text-3xl font-semibold my-6">{{ newSnippet.title }}</h2>
        <Button
          label="Rename"
          icon="pi pi-pencil"
          class="p-button-md"
          @click="editTitleDialogVisible = true"
        />
      </div>
      <div class="flex items-center gap-3">
        <Button
          label="Delete Snippet"
          icon="pi pi-trash"
          class="p-button-md p-button-danger"
          @click="deleteSnippetDialogVisible = true"
        />
        <Button v-if="hasChanges" label="Undo Changes" class="h-fit" @click="onUndoChanges" />
        <Button v-if="hasChanges" label="Save" class="h-fit" @click="onSaveChanges" />
      </div>
    </div>
    <div class="my-8 flex flex-col gap-6">
      <MultiSelect
        v-model="newTagsOnSnippet"
        showClear
        :options="tags"
        optionLabel="name"
        filter
        placeholder="Select Tags"
        class="w-full md:w-80"
      />
      <div>
        <FloatLabel variant="in">
          <Textarea
            v-model="newSnippet.description"
            inputId="description"
            variant="filled"
            fluid
            :feedback="false"
            rows="5"
            cols="30"
          />
          <label for="description">Description</label>
        </FloatLabel>
      </div>
    </div>
    <div class="w-full flex justify-between items-center">
      <h2 class="text-3xl font-semibold my-6">Files</h2>
      <Button label="Add File" class="mb-4" @click="addFileDialogVisible = true" />
    </div>
    <div v-if="files.length === 0" class="text-center">No files attached to this snippet.</div>
    <FileCard
      v-for="file in files"
      :key="file.id"
      :file="file"
      :languages="languages"
      :collapsed="file.id !== files[0]?.id"
      :snippetId="snippetId"
      :onAfterUpdate="onAfterUpdateFile"
      :onAfterDelete="onAfterUpdateFile"
    />
  </div>

  <Dialog
    v-model:visible="editTitleDialogVisible"
    modal
    header="Eddit Snippet Title"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel variant="in">
        <InputText
          v-model="newSnippet.title"
          inputId="title"
          variant="filled"
          fluid
          :feedback="false"
        />
        <label for="title">Title</label>
      </FloatLabel>
    </div>
    <div class="flex justify-end gap-2">
      <Button type="button" label="Cancel" severity="secondary" @click="onCancelEditTitle"></Button>
      <Button type="button" label="Continue" @click="editTitleDialogVisible = false"></Button>
    </div>
  </Dialog>

  <Dialog
    v-model:visible="deleteSnippetDialogVisible"
    modal
    header="Delete snippet"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      Are you sure you want to delete the snippet "{{ snippet?.title }}"? This action cannot be
      undone.
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        class="p-button-md"
        severity="secondary"
        @click="deleteSnippetDialogVisible = false"
      ></Button>
      <Button
        type="button"
        label="Delete"
        icon="pi pi-trash"
        class="p-button-md p-button-danger"
        @click="onDeleteSnippet"
      ></Button>
    </div>
  </Dialog>

  <Dialog
    v-model:visible="addFileDialogVisible"
    modal
    header="Add File"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel variant="in">
        <InputText
          v-model="newFileFilename"
          inputId="filename"
          variant="filled"
          fluid
          :feedback="false"
        />
        <label for="filename">File Name</label>
      </FloatLabel>
    </div>
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel class="w-full" variant="in">
        <Select
          v-model="newFileLanguage"
          inputId="new-file-language"
          :options="languages"
          optionLabel="name"
          class="w-full"
          variant="filled"
          :feedback="false"
          filter
        />
        <label for="new-file-language">Language</label>
      </FloatLabel>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        severity="secondary"
        @click="addFileDialogVisible = false"
      ></Button>
      <Button type="button" label="Add file" @click="onAddFile"></Button>
    </div>
  </Dialog>
</template>
