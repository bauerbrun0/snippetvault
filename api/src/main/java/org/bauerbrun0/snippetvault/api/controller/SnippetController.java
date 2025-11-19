package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.dto.CreateSnippetRequest;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
