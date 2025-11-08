<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Password from 'primevue/password'
import { userStore } from '@/stores/user'
import { loginUser } from '@/api/user'
import { isResponseError } from 'up-fetch'
import { errorSchema } from '@/utils/zod'

import { reactive } from 'vue'
import { useToast } from 'primevue/usetoast'
import { useRouter } from 'vue-router'

const router = useRouter()
const toast = useToast()

const loginForm = reactive({
  username: '',
  password: '',
})

async function submitLogin() {
  try {
    const response = await loginUser(loginForm.username, loginForm.password)
    userStore.setUser({
      id: response.id,
      username: response.username,
      created: response.created,
      isAdmin: response.isAdmin,
    })
    userStore.setToken(response.token)

    toast.add({
      severity: 'success',
      summary: 'Login Successful',
      detail: `Welcome back, ${response.username}!`,
      life: 3000,
    })
    router.push('/snippets')
  } catch (error) {
    let toastMessage = 'Failed to log in user.'
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
  <h2 class="text-2xl mb-4 mt-10 w-full md:w-1/3 mx-auto font-semibold">Welcome Back!</h2>
  <form class="flex flex-col gap-5 w-full md:w-1/3 mx-auto" @submit.prevent="submitLogin()">
    <FloatLabel variant="in">
      <InputText
        v-model="loginForm.username"
        inputId="username"
        variant="filled"
        fluid
        :feedback="false"
      />
      <label for="username">Username</label>
    </FloatLabel>
    <FloatLabel variant="in">
      <Password
        v-model="loginForm.password"
        inputId="password"
        variant="filled"
        fluid
        :feedback="false"
      />
      <label for="password">Password</label>
    </FloatLabel>
    <Button label="Login" type="submit" />
  </form>
</template>

<style scoped></style>
