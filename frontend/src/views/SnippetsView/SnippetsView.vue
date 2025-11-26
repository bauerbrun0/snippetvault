<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import MultiSelect from 'primevue/multiselect'
import Textarea from 'primevue/textarea'
import Paginator, { type PageState } from 'primevue/paginator'
import Dialog from 'primevue/dialog'
import FloatLabel from 'primevue/floatlabel'
import { useTitle } from '@vueuse/core'
import type { Tag } from '@/types/tag'
import type { Language } from '@/types/language'
import { fetchTags } from '@/api/tag'
import { fetchLanguages } from '@/api/language'
import { fetchPaginatedSnippets, createSnippet } from '@/api/snippet'
import type { DetailedSnippet } from '@/types/snippetSearchResult'
import { isResponseError } from 'up-fetch'
import { useToast } from 'primevue/usetoast'
import { errorSchema } from '@/utils/zod'
import SnippetCard from './components/SnippetCard.vue'

useTitle('SnippetVault')

const toast = useToast()

const searchQuery = ref('')
const selectedTags = ref([])
const tags = ref<Tag[]>([])
const selectedLanguages = ref([])
const languages = ref<Language[]>([])

const page = ref(0)
const rows = ref(10)
const first = computed(() => page.value * rows.value)
const totalRecords = ref(10)

const loadingSnippets = ref(false)
const snippets = ref<DetailedSnippet[]>([])

const newSnippetDialogVisible = ref(false)
const newSnippetTitle = ref('')
const newSnippetDescription = ref('')

onMounted(() => {
  loadTags()
  loadLanguages()
  loadSnippets()
})

async function loadSnippets() {
  loadingSnippets.value = true
  try {
    const snippetsResponse = await fetchPaginatedSnippets(
      searchQuery.value,
      selectedTags.value.length === 0 ? null : selectedTags.value.map((tag: Tag) => tag.id),
      selectedLanguages.value.length === 0
        ? null
        : selectedLanguages.value.map((language: Language) => language.id),
      page.value + 1,
      rows.value,
    )
    snippets.value = snippetsResponse.snippets
    totalRecords.value = snippetsResponse.totalCount
  } catch (error) {
    let toastMessage = 'Failed to fetch snippets.'
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
  loadingSnippets.value = false
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

function onSearchClick() {
  loadSnippets()
}

function onPageChange(event: PageState) {
  page.value = event.page
  rows.value = event.rows
  loadSnippets()
}

async function onCreateSnippet() {
  try {
    await createSnippet(newSnippetTitle.value, newSnippetDescription.value)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Snippet created.', life: 3000 })
    newSnippetTitle.value = ''
    newSnippetDescription.value = ''
    newSnippetDialogVisible.value = false

    searchQuery.value = ''
    selectedTags.value = []
    selectedLanguages.value = []
    page.value = 0
    rows.value = 5
    loadSnippets()
  } catch (error) {
    let toastMessage = 'Failed to create new snippet.'
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
</script>

<template>
  <div class="w-full flex justify-between items-center">
    <h2 class="text-3xl font-semibold my-6">Snippets</h2>
    <Button label="New Snippet" class="h-fit" @click="newSnippetDialogVisible = true" />
  </div>
  <div class="w-full">
    <div class="flex items-center gap-4 mx-auto md:w-fit">
      <InputText
        type="text"
        class="w-full md:w-2xl"
        v-model="searchQuery"
        placeholder="Search for snippets"
      />
      <Button label="Search" @click="onSearchClick" />
    </div>
    <div class="mt-6 w-fit mx-auto flex gap-4">
      <MultiSelect
        v-model="selectedTags"
        showClear
        :options="tags"
        optionLabel="name"
        filter
        placeholder="Select Tags"
        class="w-full md:w-80"
      />
      <MultiSelect
        v-model="selectedLanguages"
        showClear
        :options="languages"
        optionLabel="name"
        filter
        placeholder="Select Languages"
        class="w-full md:w-80"
      />
    </div>
  </div>
  <div class="my-8">
    <div v-if="loadingSnippets" class="text-center">Loading snippets...</div>
    <SnippetCard
      v-else
      v-for="snippet in snippets"
      :key="snippet.id"
      :snippet="snippet"
      :tags="tags"
      :languages="languages"
    />
    <Paginator
      :rows="5"
      :first="first"
      :totalRecords="totalRecords"
      :rowsPerPageOptions="[1, 5, 10, 20, 30]"
      @page="onPageChange"
    ></Paginator>
  </div>

  <Dialog
    v-model:visible="newSnippetDialogVisible"
    modal
    header="Create New Snippet"
    :style="{ width: '40rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel variant="in">
        <InputText
          v-model="newSnippetTitle"
          inputId="title"
          variant="filled"
          fluid
          :feedback="false"
        />
        <label for="title">Title</label>
      </FloatLabel>
    </div>
    <div class="flex items-center gap-4 mb-8">
      <FloatLabel variant="in">
        <Textarea
          v-model="newSnippetDescription"
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
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        severity="secondary"
        @click="newSnippetDialogVisible = false"
      ></Button>
      <Button type="button" label="Create" @click="onCreateSnippet"></Button>
    </div>
  </Dialog>
</template>
