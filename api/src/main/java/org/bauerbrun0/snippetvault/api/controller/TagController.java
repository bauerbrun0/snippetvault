package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.dto.CreateTagRequest;
import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.TagService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("")
    public Tag createTag(
            @Valid @RequestBody CreateTagRequest createTagRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return this.tagService.createTag(
                userDetails.getId(), createTagRequest.getName(), createTagRequest.getColor()
        );
    }
}
