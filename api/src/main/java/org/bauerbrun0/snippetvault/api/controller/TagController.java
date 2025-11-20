package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.dto.CreateTagRequest;
import org.bauerbrun0.snippetvault.api.dto.UpdateTagRequest;
import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.TagService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("")
    public List<Tag> getTags(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return this.tagService.getTags(userDetails.getId());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@tagSecurity.isOwner(authentication, #id)")
    public Tag updateTag(@P("id") @PathVariable("id") Long id, @Valid @RequestBody UpdateTagRequest updateTagRequest) {
        return this.tagService.updateTag(
                id, updateTagRequest.getName(), updateTagRequest.getColor()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@tagSecurity.isOwner(authentication, #id)")
    public Tag deleteTag(@P("id") @PathVariable("id") Long id) {
        return this.tagService.deleteTag(id);
    }

    @GetMapping("/{id}")
    @PostAuthorize("returnObject.userId == authentication.principal.id")
    public Tag getTag(@P("id") @PathVariable("id") Long id) {
        return this.tagService.getTag(id);
    }

}
