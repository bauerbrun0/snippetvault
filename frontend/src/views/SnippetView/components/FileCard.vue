<script setup lang="ts">
import type { File } from '@/types/file'
import Panel from 'primevue/panel'
import Button from 'primevue/button'
import Select from 'primevue/select'
import type { Language } from '@/types/language'
import { ref, computed } from 'vue'
import Textarea from 'primevue/textarea'
import FloatLabel from 'primevue/floatlabel'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import { deleteFileFromSnippet, updateFileInSnippet } from '@/api/snippet'
import { isResponseError } from 'up-fetch'
import { useToast } from 'primevue/usetoast'
import { errorSchema } from '@/utils/zod'

const props = defineProps<{
  file: File
  languages: Language[]
  collapsed: boolean
  snippetId: number
  onAfterDelete?: (file: File) => void
  onAfterUpdate?: (file: File) => void
}>()

const toast = useToast()

const language = ref<Language>(
  props.languages.find((lang) => lang.id === props.file.languageId) as Language,
)

const content = ref(props.file.content)
const filename = ref(props.file.filename)

const hasChanges = computed(() => {
  return (
    content.value !== props.file.content ||
    language.value.id !== props.file.languageId ||
    filename.value !== props.file.filename
  )
})

const editFilenameDialogVisible = ref(false)
const deleteFileDialogVisible = ref(false)

function undoChanges() {
  content.value = props.file.content
  filename.value = props.file.filename
  language.value = props.languages.find((lang) => lang.id === props.file.languageId) as Language
}

function onCancelEditFilename() {
  editFilenameDialogVisible.value = false
  filename.value = props.file.filename
}

async function onDeleteFile() {
  try {
    const deletedFile = await deleteFileFromSnippet(props.snippetId, props.file.id)
    toast.add({ severity: 'success', summary: 'Success', detail: 'File deleted.', life: 3000 })
    deleteFileDialogVisible.value = false
    if (props.onAfterUpdate) {
      props.onAfterUpdate(deletedFile)
    }
  } catch (error) {
    let toastMessage = 'Failed to delete file.'
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

async function saveChanges() {
  try {
    const updatedFile = await updateFileInSnippet(
      props.snippetId,
      props.file.id,
      filename.value,
      content.value,
      language.value.id,
    )
    toast.add({ severity: 'success', summary: 'Success', detail: 'File updated.', life: 3000 })
    if (props.onAfterUpdate) {
      props.onAfterUpdate(updatedFile)
    }
  } catch (error) {
    let toastMessage = 'Failed to update file.'
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
  <Panel toggleable class="mb-3" :collapsed="collapsed">
    <template #header>
      <div class="flex w-full items-center justify-between my-2">
        <div class="flex items-center gap-3">
          <span class="font-bold">
            <code>{{ filename }}</code>
          </span>
          <Button
            icon="pi pi-pencil"
            class="p-button-sm mx-1"
            label="Rename"
            @click="editFilenameDialogVisible = true"
          />
        </div>
        <div class="flex items-center gap-3 mr-3">
          <Button
            icon="pi pi-trash"
            class="p-button-sm p-button-danger mx-1"
            label="Delete"
            @click="deleteFileDialogVisible = true"
          />
          <Button
            v-if="hasChanges"
            icon="pi"
            class="p-button-sm"
            label="Undo Changes"
            @click="undoChanges"
          />
          <Button
            v-if="hasChanges"
            icon="pi"
            class="p-button-sm"
            label="Save"
            @click="saveChanges"
          />
          <Select
            size="small"
            v-model="language"
            inputId="new-file-language"
            :options="languages"
            optionLabel="name"
            variant="filled"
            :feedback="false"
            filter
          />
        </div>
      </div>
    </template>
    <div>
      <FloatLabel variant="in">
        <Textarea
          v-model="content"
          :inputId="'file-content-' + file.id"
          variant="filled"
          fluid
          :feedback="false"
          rows="5"
          cols="30"
        />
        <label :for="'file-content-' + file.id">Content</label>
      </FloatLabel>
    </div>
  </Panel>

  <Dialog
    v-model:visible="editFilenameDialogVisible"
    modal
    header="Eddit File Name"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel variant="in">
        <InputText
          v-model="filename"
          :inputId="'title' + file.id"
          variant="filled"
          fluid
          :feedback="false"
        />
        <label :for="'title' + file.id">File Name</label>
      </FloatLabel>
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        severity="secondary"
        @click="onCancelEditFilename"
      ></Button>
      <Button type="button" label="Continue" @click="editFilenameDialogVisible = false"></Button>
    </div>
  </Dialog>

  <Dialog
    v-model:visible="deleteFileDialogVisible"
    modal
    header="Delete file"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      Are you sure you want to delete the file '{{ filename }}'? This action cannot be undone.
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        class="p-button-md"
        severity="secondary"
        @click="deleteFileDialogVisible = false"
      ></Button>
      <Button
        type="button"
        label="Delete"
        icon="pi pi-trash"
        class="p-button-md p-button-danger"
        @click="onDeleteFile"
      ></Button>
    </div>
  </Dialog>
</template>
