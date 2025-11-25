package org.bauerbrun0.snippetvault.api.security;

import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("snippetSecurity")
public class SnippetSecurity {
    private final SnippetService snippetService;

    public SnippetSecurity(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    public boolean isOwner(Authentication authentication, Long snippetId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Snippet snippet = this.snippetService.getSnippet(snippetId);
        if (snippet == null) {
            return true;
        }
        return snippet.getUserId().equals(userDetails.getId());
    }
}
