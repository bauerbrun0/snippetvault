import z from 'zod'
import { upfetch } from './api'
import { dateArraySchema } from '@/utils/zod'
import type { Tag } from '@/types/tag'

export async function fetchTags(): Promise<Tag[]> {
  const result = await upfetch('/tags', {
    schema: z.array(
      z.object({
        id: z.number(),
        userId: z.number(),
        name: z.string(),
        color: z.string(),
        created: dateArraySchema,
      }),
    ),
  })

  return result
}
