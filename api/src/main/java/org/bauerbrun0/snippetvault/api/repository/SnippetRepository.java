package org.bauerbrun0.snippetvault.api.repository;


import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.model.File;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.SnippetSearchResult;
import org.bauerbrun0.snippetvault.api.model.Tag;

import java.util.List;

public interface SnippetRepository {
    Snippet createSnippet(Long userId, String title, String description) throws UserNotFoundException;
    void addTagToSnippet(Long snippetId, Long tagId) throws TagNotFoundException, SnippetNotFoundException, DuplicateTagOnSnippetException;
    void removeTagFromSnippet(Long snippetId, Long tagId) throws TagNotOnSnippetException;
    List<Tag> getTagsOfSnippet(Long snippetId);
    SnippetSearchResult getPaginatedSnippets(
            Long userId, String searchQuery, List<Long> tagIds, List<Long> languageIds, Long pageNumber, Long pageSize
    );
    Snippet getSnippet(Long snippetId);
    Snippet deleteSnippet(Long snippetId);
    Snippet updateSnippet(Long snippetId, String title, String description);

    File createFile(Long snippetId, String filename, String content, Long languageId);
    List<File> getFiles(Long snippetId);
    File updateFile(Long fileId, Long snippetId, String filename, String content, Long languageId);
    File deleteFile(Long fileId, Long snippetId);
}
