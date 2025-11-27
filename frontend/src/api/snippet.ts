import z from 'zod'
import { upfetch } from './api'
import { dateArraySchema } from '@/utils/zod'
import type { SnippetSearchResult } from '@/types/snippetSearchResult'
import type { Snippet } from '@/types/snippet'
import type { Tag } from '@/types/tag'
import type { File } from '@/types/file'

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

export async function fetchSnippetById(id: number): Promise<Snippet> {
  const result = await upfetch(`/snippets/${id}`, {
    method: 'GET',
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

export async function updateSnippet(
  id: number,
  title: string,
  description: string,
): Promise<Snippet> {
  const result = await upfetch(`/snippets/${id}`, {
    method: 'PATCH',
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

export async function deleteSnippet(id: number): Promise<Snippet> {
  const result = await upfetch(`/snippets/${id}`, {
    method: 'DELETE',
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

export async function fetchTagsForSnippet(snippetId: number): Promise<Tag[]> {
  const result = await upfetch(`/snippets/${snippetId}/tags`, {
    method: 'GET',
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

export async function addTagToSnippet(snippetId: number, tagId: number): Promise<void> {
  await upfetch(`/snippets/${snippetId}/tags/${tagId}`, {
    method: 'POST',
  })
}

export async function removeTagFromSnippet(snippetId: number, tagId: number): Promise<void> {
  await upfetch(`/snippets/${snippetId}/tags/${tagId}`, {
    method: 'DELETE',
  })
}

export async function fetchFilesForSnippet(snippetId: number): Promise<File[]> {
  const result = await upfetch(`/snippets/${snippetId}/files`, {
    method: 'GET',
    schema: z.array(
      z.object({
        id: z.number(),
        snippetId: z.number(),
        filename: z.string(),
        content: z.string(),
        languageId: z.number(),
        created: dateArraySchema,
        updated: dateArraySchema,
      }),
    ),
  })

  return result
}

export async function createFileForSnippet(
  snippetId: number,
  fileName: string,
  content: string,
  languageId: number,
): Promise<File> {
  const result = await upfetch(`/snippets/${snippetId}/files`, {
    method: 'POST',
    body: { fileName, content, languageId },
    schema: z.object({
      id: z.number(),
      snippetId: z.number(),
      filename: z.string(),
      content: z.string(),
      languageId: z.number(),
      created: dateArraySchema,
      updated: dateArraySchema,
    }),
  })

  return result
}

export async function deleteFileFromSnippet(snippetId: number, fileId: number): Promise<File> {
  const result = await upfetch(`/snippets/${snippetId}/files/${fileId}`, {
    method: 'DELETE',
    schema: z.object({
      id: z.number(),
      snippetId: z.number(),
      filename: z.string(),
      content: z.string(),
      languageId: z.number(),
      created: dateArraySchema,
      updated: dateArraySchema,
    }),
  })

  return result
}

export async function updateFileInSnippet(
  snippetId: number,
  fileId: number,
  filename: string,
  content: string,
  languageId: number,
): Promise<File> {
  const result = await upfetch(`/snippets/${snippetId}/files/${fileId}`, {
    method: 'PATCH',
    body: { filename, content, languageId },
    schema: z.object({
      id: z.number(),
      snippetId: z.number(),
      filename: z.string(),
      content: z.string(),
      languageId: z.number(),
      created: dateArraySchema,
      updated: dateArraySchema,
    }),
  })

  return result
}
