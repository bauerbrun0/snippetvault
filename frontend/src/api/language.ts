import z from 'zod'
import { upfetch } from './api'
import type { Language } from '@/types/language'

export async function fetchLanguages(): Promise<Language[]> {
  const result = await upfetch('/languages', {
    schema: z.array(
      z.object({
        id: z.number(),
        name: z.string(),
      }),
    ),
  })

  return result
}
