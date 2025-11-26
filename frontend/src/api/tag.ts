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

export async function updateTag(tagId: number, name: string, color: string): Promise<Tag> {
  const result = await upfetch(`/tags/${tagId}`, {
    method: 'PATCH',
    body: { name, color },
    schema: z.object({
      id: z.number(),
      userId: z.number(),
      name: z.string(),
      color: z.string(),
      created: dateArraySchema,
    }),
  })

  return result
}

export async function deleteTag(tagId: number): Promise<Tag> {
  const result = await upfetch(`/tags/${tagId}`, {
    method: 'DELETE',
    schema: z.object({
      id: z.number(),
      userId: z.number(),
      name: z.string(),
      color: z.string(),
      created: dateArraySchema,
    }),
  })

  return result
}

export async function createTag(name: string, color: string): Promise<Tag> {
  const result = await upfetch('/tags', {
    method: 'POST',
    body: { name, color },
    schema: z.object({
      id: z.number(),
      userId: z.number(),
      name: z.string(),
      color: z.string(),
      created: dateArraySchema,
    }),
  })

  return result
}
