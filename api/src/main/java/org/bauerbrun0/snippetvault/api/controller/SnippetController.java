package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.model.CreateSnippetRequest;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.User;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.bauerbrun0.snippetvault.api.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {
    private final SnippetService snippetService;
    private final UserService userService;

    public SnippetController(SnippetService snippetService, UserService userService) {
        this.snippetService = snippetService;
        this.userService = userService;
    }

    @PostMapping("")
    public Snippet createSnippet(
            @Valid @RequestBody CreateSnippetRequest createSnippetRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = this.userService.getUserByUsername(userDetails.getUsername());
        return this.snippetService.create(
                user.getId(),
                createSnippetRequest.getTitle(),
                createSnippetRequest.getDescription()
        );
    }
}
