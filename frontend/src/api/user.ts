import z from 'zod'
import { upfetch } from './api'
import { dateArraySchema } from '@/utils/zod'

export async function fetchAuthenticatedUser() {
  const result = await upfetch('/auth/current-user', {
    schema: z
      .object({
        userId: z.number(),
        username: z.string(),
        created: dateArraySchema,
        admin: z.boolean(),
      })
      .transform((data) => ({
        id: data.userId,
        username: data.username,
        created: data.created,
        isAdmin: data.admin,
      })),
  })

  return result
}

export async function loginUser(username: string, password: string) {
  const result = await upfetch('/auth/login', {
    method: 'POST',
    body: { username, password },
    schema: z
      .object({
        token: z.string(),
        userId: z.number(),
        username: z.string(),
        created: dateArraySchema,
        admin: z.boolean(),
      })
      .transform((data) => ({
        token: data.token,
        id: data.userId,
        username: data.username,
        created: data.created,
        isAdmin: data.admin,
      })),
  })

  return result
}
