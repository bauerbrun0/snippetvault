<script setup lang="ts">
import type { Tag } from '@/types/tag'
import Card from 'primevue/card'
import Button from 'primevue/button'
import { getLuminance } from 'color2k'
import Dialog from 'primevue/dialog'
import ColorPicker from 'primevue/colorpicker'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import { updateTag, deleteTag } from '@/api/tag'
import { isResponseError } from 'up-fetch'
import { useToast } from 'primevue/usetoast'
import { errorSchema } from '@/utils/zod'
import { ref } from 'vue'

const props = defineProps<{
  tag: Tag
  onAfterUpdate?: () => void
  onAfterDelete?: () => void
}>()

const toast = useToast()

const editTagDialogVisible = ref(false)
const editTagName = ref(props.tag.name)
const editTagColor = ref(props.tag.color.slice(1))

const deleteTagDialogVisible = ref(false)

async function onSave() {
  try {
    await updateTag(props.tag.id, editTagName.value, `#${editTagColor.value}`)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Tag updated.', life: 3000 })
    editTagDialogVisible.value = false
  } catch (error) {
    let toastMessage = 'Failed to update tag.'
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

  if (props.onAfterUpdate) {
    props.onAfterUpdate()
  }
}

async function onDelete() {
  try {
    await deleteTag(props.tag.id)
    toast.add({ severity: 'success', summary: 'Success', detail: 'Tag deleted.', life: 3000 })
    deleteTagDialogVisible.value = false
  } catch (error) {
    let toastMessage = 'Failed to delete tag.'
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

  if (props.onAfterDelete) {
    props.onAfterDelete()
  }
}
</script>

<template>
  <Card class="mb-3 hover:bg-surface-800!">
    <template #title>
      <div class="flex justify-between gap-4 items-center">
        <h2>{{ tag.name }}</h2>
        <div class="flex gap-2 items-center">
          <Button
            label="Edit"
            icon="pi pi-pencil"
            class="p-button-sm"
            @click="editTagDialogVisible = true"
          />
          <Button
            label="Delete"
            icon="pi pi-trash"
            class="p-button-sm p-button-danger"
            @click="deleteTagDialogVisible = true"
          />
        </div>
      </div>
    </template>
    <template #content>
      <div
        class="text-sm rounded-md px-2 py-1 w-fit"
        :style="{
          backgroundColor: tag.color,
          color: getLuminance(tag.color) > 0.5 ? 'black' : 'white',
        }"
      >
        {{ tag.color }}
      </div>
    </template>
  </Card>

  <Dialog
    v-model:visible="editTagDialogVisible"
    modal
    header="Edit tag"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      <FloatLabel variant="in">
        <InputText v-model="editTagName" inputId="name" variant="filled" fluid :feedback="false" />
        <label for="name">Name</label>
      </FloatLabel>
    </div>
    <div class="flex items-center gap-4 my-8">
      <label for="color">Color</label>
      <ColorPicker v-model="editTagColor" inputId="color" format="hex" class="" />
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        severity="secondary"
        @click="editTagDialogVisible = false"
      ></Button>
      <Button type="button" label="Save" @click="onSave"></Button>
    </div>
  </Dialog>

  <Dialog
    v-model:visible="deleteTagDialogVisible"
    modal
    header="Delete tag"
    :style="{ width: '35rem' }"
  >
    <div class="flex items-center gap-4 mb-4">
      Are you sure you want to delete the tag "{{ tag.name }}"? This action cannot be undone.
    </div>
    <div class="flex justify-end gap-2">
      <Button
        type="button"
        label="Cancel"
        class="p-button-md"
        severity="secondary"
        @click="deleteTagDialogVisible = false"
      ></Button>
      <Button
        type="button"
        label="Delete"
        icon="pi pi-trash"
        class="p-button-md p-button-danger"
        @click="onDelete"
      ></Button>
    </div>
  </Dialog>
</template>
