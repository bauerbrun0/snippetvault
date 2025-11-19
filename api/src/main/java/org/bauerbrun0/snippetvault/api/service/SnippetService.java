package org.bauerbrun0.snippetvault.api.service;

import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.repository.SnippetRepository;
import org.springframework.stereotype.Service;

@Service
public class SnippetService {
    private final SnippetRepository snippetRepository;

    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    public Snippet create(Long userId, String title, String description) {
        return this.snippetRepository.createSnippet(userId, title, description);
    }
}
