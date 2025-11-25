package org.bauerbrun0.snippetvault.api.service;

import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.SnippetSearchResult;
import org.bauerbrun0.snippetvault.api.repository.SnippetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SnippetService {
    private final SnippetRepository snippetRepository;

    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    @Transactional
    public Snippet create(Long userId, String title, String description) {
        return this.snippetRepository.createSnippet(userId, title, description);
    }

    @Transactional
    public void addTagToSnippet(Long snippetId, Long tagId) {
        this.snippetRepository.addTagToSnippet(snippetId, tagId);
    }

    @Transactional
    public void removeTagFromSnippet(Long snippetId, Long tagId) {
        this.snippetRepository.removeTagFromSnippet(snippetId, tagId);
    }

    @Transactional(readOnly = true)
    public SnippetSearchResult getPaginatedSnippets(
            Long userId, String searchQuery, List<Long> tagIds, List<Long> languageIds, Long pageNumber, Long pageSize
    ) {
        return this.snippetRepository.getPaginatedSnippets(
                userId, searchQuery, tagIds, languageIds, pageNumber, pageSize
        );
    }

    @Transactional(readOnly = true)
    public Snippet getSnippet(Long snippetId) {
        return this.snippetRepository.getSnippet(snippetId);
    }

    @Transactional
    public Snippet deleteSnippet(Long snippetId) {
        return this.snippetRepository.deleteSnippet(snippetId);
    }

    @Transactional
    public Snippet updateSnippet(Long snippetId, String title, String description) {
        return this.snippetRepository.updateSnippet(snippetId, title, description);
    }
}
