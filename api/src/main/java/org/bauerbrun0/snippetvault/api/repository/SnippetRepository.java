package org.bauerbrun0.snippetvault.api.repository;


import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.model.Snippet;

public interface SnippetRepository {
    Snippet createSnippet(Long userId, String title, String description) throws UserNotFoundException;
}
