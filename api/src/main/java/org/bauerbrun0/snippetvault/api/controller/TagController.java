package org.bauerbrun0.snippetvault.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.dto.CreateTagRequest;
import org.bauerbrun0.snippetvault.api.dto.UpdateTagRequest;
import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Manage personal snippet tags")
@SecurityRequirement(name = "bearerAuth")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(
            summary = "Create a new tag",
            description = "Creates a tag owned by the current authenticated user."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Tag successfully created",
            content = @Content(schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping("")
    public ResponseEntity<Tag> createTag(
            @Valid @RequestBody CreateTagRequest createTagRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Tag tag = this.tagService.createTag(
                userDetails.getId(),
                createTagRequest.getName(),
                createTagRequest.getColor()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }

    @Operation(
            summary = "Get all tags for current user",
            description = "Returns all tags belonging to the current authenticated user."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of tags",
            content = @Content(schema = @Schema(implementation = Tag.class))
    )
    @GetMapping("")
    public ResponseEntity<List<Tag>> getTags(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Tag> tags = this.tagService.getTags(userDetails.getId());
        return ResponseEntity.ok(tags);
    }

    @Operation(
            summary = "Update a tag",
            description = "Updates a tag. Only the owner may update the tag."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tag updated",
            content = @Content(schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(responseCode = "403", description = "Not the tag owner")
    @PatchMapping("/{id}")
    @PreAuthorize("@tagSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Tag> updateTag(@P("id") @PathVariable("id") Long id, @Valid @RequestBody UpdateTagRequest updateTagRequest) {
        Tag tag = this.tagService.updateTag(
                id, updateTagRequest.getName(), updateTagRequest.getColor()
        );
        return ResponseEntity.ok(tag);
    }

    @Operation(
            summary = "Delete a tag",
            description = "Deletes a tag. Only the owner can delete it."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tag deleted",
            content = @Content(schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(responseCode = "403", description = "Not the tag owner")
    @DeleteMapping("/{id}")
    @PreAuthorize("@tagSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Tag> deleteTag(@P("id") @PathVariable("id") Long id) {
        Tag tag = this.tagService.deleteTag(id);
        return ResponseEntity.ok(tag);
    }

    @Operation(
            summary = "Get a tag by ID",
            description = "Returns a single tag. Only the owner can view it."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tag retrieved",
            content = @Content(schema = @Schema(implementation = Tag.class))
    )
    @ApiResponse(responseCode = "403", description = "Not the tag owner")
    @GetMapping("/{id}")
    @PostAuthorize("returnObject.body.userId == authentication.principal.id")
    public ResponseEntity<Tag> getTag(@P("id") @PathVariable("id") Long id) {
        Tag tag = this.tagService.getTag(id);
        return ResponseEntity.ok(tag);
    }
}
