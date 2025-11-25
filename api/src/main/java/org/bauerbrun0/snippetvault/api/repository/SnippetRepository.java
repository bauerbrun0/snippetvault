package org.bauerbrun0.snippetvault.api.repository;


import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.SnippetSearchResult;

import java.util.List;

public interface SnippetRepository {
    Snippet createSnippet(Long userId, String title, String description) throws UserNotFoundException;
    void addTagToSnippet(Long snippetId, Long tagId) throws TagNotFoundException, SnippetNotFoundException, DuplicateTagOnSnippetException;
    void removeTagFromSnippet(Long snippetId, Long tagId) throws TagNotOnSnippetException;
    SnippetSearchResult getPaginatedSnippets(
            Long userId, String searchQuery, List<Long> tagIds, List<Long> languageIds, Long pageNumber, Long pageSize
    );
    Snippet getSnippet(Long snippetId);
    Snippet deleteSnippet(Long snippetId);
    Snippet updateSnippet(Long snippetId, String title, String description);
}
