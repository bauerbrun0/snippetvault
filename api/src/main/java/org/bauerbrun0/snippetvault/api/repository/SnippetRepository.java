package org.bauerbrun0.snippetvault.api.repository;


import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.model.Snippet;

public interface SnippetRepository {
    Snippet createSnippet(Long userId, String title, String description) throws UserNotFoundException;
    void addTagToSnippet(Long snippetId, Long tagId) throws TagNotFoundException, SnippetNotFoundException, DuplicateTagOnSnippetException;
    void removeTagFromSnippet(Long snippetId, Long tagId) throws TagNotOnSnippetException;
}
