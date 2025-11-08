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
