package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.dto.CreateSnippetRequest;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@tagSecurity.isOwner(authentication, #tagId)") // TODO: check for snippet ownership too
    public void addTagToSnippet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @P("id") @PathVariable("id") Long id,
            @P("tagId") @PathVariable("tagId") Long tagId
    ) {
        this.snippetService.addTagToSnippet(id, tagId);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@tagSecurity.isOwner(authentication, #tagId)") // TODO: check for snippet ownership too
    public void removeTagFromSnippet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @P("id") @PathVariable("id") Long id,
            @P("tagId") @PathVariable("tagId") Long tagId
    ) {
        this.snippetService.removeTagFromSnippet(id, tagId);
    }
}
