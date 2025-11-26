export type SnippetSearchResult = {
  snippets: DetailedSnippet[]
  totalCount: number
}

export type DetailedSnippet = {
  id: number
  userId: number
  title: string
  description: string
  created: Date
  updated: Date
  relevance: number
  fileCount: number
  languageIds: number[]
  tagIds: number[]
}
