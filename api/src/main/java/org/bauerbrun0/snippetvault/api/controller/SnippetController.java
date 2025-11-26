package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.dto.*;
import org.bauerbrun0.snippetvault.api.model.File;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.SnippetSearchResult;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/snippets")
public class SnippetController {
    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @PostMapping("")
    public Snippet createSnippet(
            @Valid @RequestBody CreateSnippetRequest createSnippetRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return this.snippetService.create(
                userDetails.getId(),
                createSnippetRequest.getTitle(),
                createSnippetRequest.getDescription()
        );
    }

    @PostMapping("/search")
    public SnippetSearchResult searchSnippets(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SearchSnippetsRequest searchRequest
    ) {
        SnippetSearchResult result = this.snippetService.getPaginatedSnippets(
                userDetails.getId(),
                searchRequest.getSearchQuery(),
                searchRequest.getTagIds(),
                searchRequest.getLanguageIds(),
                searchRequest.getPageNumber(),
                searchRequest.getPageSize()
        );
        return result;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public Snippet getSnippet(
            @P("id") @PathVariable("id") Long id
    ) {
        return this.snippetService.getSnippet(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public Snippet deleteSnippet(
            @P("id") @PathVariable("id") Long id
    ) {
        return this.snippetService.deleteSnippet(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public Snippet updateSnippet(
            @P("id") @PathVariable("id") Long id,
            @Valid @RequestBody UpdateSnippetRequest updateRequest
    ) {
        return this.snippetService.updateSnippet(
                id,
                updateRequest.getTitle(),
                updateRequest.getDescription()
        );
    }

    @PostMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id) && @tagSecurity.isOwner(authentication, #tagId)")
    public void addTagToSnippet(
            @P("id") @PathVariable("id") Long id,
            @P("tagId") @PathVariable("tagId") Long tagId
    ) {
        this.snippetService.addTagToSnippet(id, tagId);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id) && @tagSecurity.isOwner(authentication, #tagId)")
    public void removeTagFromSnippet(
            @P("id") @PathVariable("id") Long id,
            @P("tagId") @PathVariable("tagId") Long tagId
    ) {
        this.snippetService.removeTagFromSnippet(id, tagId);
    }

    @PostMapping("/{id}/files")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public File createFile(
            @P("id") @PathVariable("id") Long id,
            @Valid @RequestBody CreateFileRequest createFileRequest
    ) {
        return this.snippetService.createFile(
                id,
                createFileRequest.getFileName(),
                createFileRequest.getContent(),
                createFileRequest.getLanguageId()
        );
    }

    @GetMapping("/{id}/files")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public List<File> getFiles(
            @P("id") @PathVariable("id") Long id
    ) {
        return this.snippetService.getFiles(id);
    }

    @PatchMapping("/{id}/files/{fileId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public File updateFile(
            @P("id") @PathVariable("id") Long id,
            @P("fileId") @PathVariable("fileId") Long fileId,
            @Valid @RequestBody UpdateFileRequest updateFileRequest
    ) {
        return this.snippetService.updateFile(
                fileId,
                id,
                updateFileRequest.getFilename(),
                updateFileRequest.getContent(),
                updateFileRequest.getLanguageId()
        );
    }

    @DeleteMapping("/{id}/files/{fileId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public File deleteFile(
            @P("id") @PathVariable("id") Long id,
            @P("fileId") @PathVariable("fileId") Long fileId
    ) {
        return this.snippetService.deleteFile(fileId, id);
    }
}
