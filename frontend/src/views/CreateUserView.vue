<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Password from 'primevue/password'
import Checkbox from 'primevue/checkbox'
import { reactive } from 'vue'
import { useToast } from 'primevue/usetoast'
import { useRouter } from 'vue-router'
import { createUser } from '@/api/user'
import { isResponseError } from 'up-fetch'
import { errorSchema } from '@/utils/zod'

const toast = useToast()
const router = useRouter()

const createUserForm = reactive({
  username: '',
  password: '',
  passwordConfirmation: '',
  isAdmin: false,
})

async function submitCreate() {
  if (createUserForm.password !== createUserForm.passwordConfirmation) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Password and confirmation do not match.',
      life: 3000,
    })
    return
  }

  try {
    const user = await createUser(
      createUserForm.username,
      createUserForm.password,
      createUserForm.isAdmin,
    )
    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: `User ${user.username} created successfully.`,
      life: 3000,
    })
    router.push('/admin')
  } catch (error) {
    console.log(error)
    let toastMessage = 'Failed to create user.'
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
  <h2 class="text-3xl font-semibold my-6">Create User Page</h2>
  <div>
    <form class="flex flex-col gap-5 w-full md:w-1/3 mx-auto" @submit.prevent="submitCreate()">
      <h3 class="text-xl font-medium mb-4">Create a New User</h3>
      <FloatLabel variant="in">
        <InputText
          v-model="createUserForm.username"
          inputId="username"
          variant="filled"
          fluid
          :feedback="false"
        />
        <label for="username">Username</label>
      </FloatLabel>
      <FloatLabel variant="in">
        <Password v-model="createUserForm.password" inputId="password" variant="filled" fluid />
        <label for="password">Password</label>
      </FloatLabel>
      <FloatLabel variant="in">
        <Password
          v-model="createUserForm.passwordConfirmation"
          inputId="confirm-password"
          variant="filled"
          :feedback="false"
          fluid
        />
        <label for="confirm-password">Confirm Password</label>
      </FloatLabel>
      <div class="flex items-center gap-2">
        <Checkbox v-model="createUserForm.isAdmin" inputId="isadmin" name="isadmin" binary />
        <label for="isadmin">Admin</label>
      </div>
      <Button label="Create User" type="submit" />
    </form>
  </div>
</template>
