import z from 'zod'
import { upfetch } from './api'
import { dateArraySchema } from '@/utils/zod'
import type { SnippetSearchResult } from '@/types/snippetSearchResult'
import type { Snippet } from '@/types/snippet'

export async function fetchPaginatedSnippets(
  searchQuery: string,
  tagIds: number[] | null,
  languageIds: number[] | null,
  pageNumber: number,
  pageSize: number,
): Promise<SnippetSearchResult> {
  const result = await upfetch('/snippets/search', {
    method: 'POST',
    body: { searchQuery, tagIds, languageIds, pageNumber, pageSize },
    schema: z.object({
      totalCount: z.number(),
      snippets: z.array(
        z.object({
          id: z.number(),
          userId: z.number(),
          title: z.string(),
          description: z.string(),
          created: dateArraySchema,
          updated: dateArraySchema,
          relevance: z.number(),
          fileCount: z.number(),
          languageIds: z.array(z.number()),
          tagIds: z.array(z.number()),
        }),
      ),
    }),
  })

  return result
}

export async function createSnippet(title: string, description: string): Promise<Snippet> {
  const result = await upfetch('/snippets', {
    method: 'POST',
    body: { title, description },
    schema: z.object({
      id: z.number(),
      userId: z.number(),
      title: z.string(),
      description: z.string(),
      created: dateArraySchema,
      updated: dateArraySchema,
    }),
  })

  return result
}
