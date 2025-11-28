package org.bauerbrun0.snippetvault.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.dto.*;
import org.bauerbrun0.snippetvault.api.model.File;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.SnippetSearchResult;
import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@io.swagger.v3.oas.annotations.tags.Tag(name = "Snippets", description = "Create and manage code snippets, files, and tags")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/snippets")
public class SnippetController {
    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @Operation(
            summary = "Create a new snippet",
            description = "Creates a snippet for the authenticated user."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Snippet created",
            content = @Content(schema = @Schema(implementation = Snippet.class))
    )
    @PostMapping("")
    public ResponseEntity<Snippet> createSnippet(
            @Valid @RequestBody CreateSnippetRequest createSnippetRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Snippet snippet = snippetService.create(
                userDetails.getId(),
                createSnippetRequest.getTitle(),
                createSnippetRequest.getDescription()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(snippet);
    }

    @Operation(
            summary = "Search and filter snippets",
            description = "Returns paginated snippets filtered by query, tags, language, etc."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Search results",
            content = @Content(schema = @Schema(implementation = SnippetSearchResult.class))
    )
    @PostMapping("/search")
    public ResponseEntity<SnippetSearchResult> searchSnippets(
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
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get snippet",
            description = "Fetch a snippet by ID (owner only)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Snippet retrieved",
            content = @Content(schema = @Schema(implementation = Snippet.class))
    )
    @ApiResponse(responseCode = "403", description = "Not the owner")
    @GetMapping("/{id}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Snippet> getSnippet(
            @P("id") @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(this.snippetService.getSnippet(id));
    }

    @Operation(
            summary = "Delete snippet",
            description = "Deletes a snippet (owner only)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Snippet deleted",
            content = @Content(schema = @Schema(implementation = Snippet.class))
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Snippet> deleteSnippet(
            @P("id") @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(this.snippetService.deleteSnippet(id));
    }

    @Operation(
            summary = "Update snippet",
            description = "Updates title/description (owner only)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Snippet updated",
            content = @Content(schema = @Schema(implementation = Snippet.class))
    )
    @PatchMapping("/{id}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Snippet> updateSnippet(
            @P("id") @PathVariable("id") Long id,
            @Valid @RequestBody UpdateSnippetRequest updateRequest
    ) {
        Snippet updated = snippetService.updateSnippet(
                id,
                updateRequest.getTitle(),
                updateRequest.getDescription()
        );
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Get snippet tags",
            description = "Returns all tags attached to a snippet."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of tags",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tag.class)))
    )
    @GetMapping("/{id}/tags")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<List<Tag>> getTagsOfSnippet(
            @P("id") @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(this.snippetService.getTagsOfSnippet(id));
    }

    @Operation(
            summary = "Add tag to snippet",
            description = "Adds a tag to the snippet (user must own both)."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Tag added to snippet"
    )
    @PostMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id) && @tagSecurity.isOwner(authentication, #tagId)")
    public ResponseEntity<Void> addTagToSnippet(
            @P("id") @PathVariable("id") Long id,
            @P("tagId") @PathVariable("tagId") Long tagId
    ) {
        this.snippetService.addTagToSnippet(id, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove tag from snippet",
            description = "Removes a tag from a snippet (owner only)."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Tag removed"
    )
    @DeleteMapping("/{id}/tags/{tagId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id) && @tagSecurity.isOwner(authentication, #tagId)")
    public ResponseEntity<Void> removeTagFromSnippet(
            @P("id") @PathVariable("id") Long id,
            @P("tagId") @PathVariable("tagId") Long tagId
    ) {
        snippetService.removeTagFromSnippet(id, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Create file",
            description = "Adds a code file to a snippet."
    )
    @ApiResponse(
            responseCode = "201",
            description = "File created",
            content = @Content(schema = @Schema(implementation = File.class))
    )
    @PostMapping("/{id}/files")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<File> createFile(
            @P("id") @PathVariable("id") Long id,
            @Valid @RequestBody CreateFileRequest createFileRequest
    ) {
        File file = this.snippetService.createFile(
                id,
                createFileRequest.getFileName(),
                createFileRequest.getContent(),
                createFileRequest.getLanguageId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(file);
    }

    @Operation(
            summary = "Get snippet files",
            description = "Returns all files inside a snippet."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of files",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = File.class)))
    )
    @GetMapping("/{id}/files")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<List<File>> getFiles(
            @P("id") @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(this.snippetService.getFiles(id));
    }

    @Operation(
            summary = "Update file",
            description = "Updates a snippet file’s contents, language or filename."
    )
    @ApiResponse(
            responseCode = "200",
            description = "File updated",
            content = @Content(schema = @Schema(implementation = File.class))
    )
    @PatchMapping("/{id}/files/{fileId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<File> updateFile(
            @P("id") @PathVariable("id") Long id,
            @P("fileId") @PathVariable("fileId") Long fileId,
            @Valid @RequestBody UpdateFileRequest updateFileRequest
    ) {
        File updated = this.snippetService.updateFile(
                fileId,
                id,
                updateFileRequest.getFilename(),
                updateFileRequest.getContent(),
                updateFileRequest.getLanguageId()
        );
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete file",
            description = "Deletes a file from a snippet."
    )
    @ApiResponse(
            responseCode = "200",
            description = "File deleted",
            content = @Content(schema = @Schema(implementation = File.class))
    )
    @DeleteMapping("/{id}/files/{fileId}")
    @PreAuthorize("@snippetSecurity.isOwner(authentication, #id)")
    public ResponseEntity<File> deleteFile(
            @P("id") @PathVariable("id") Long id,
            @P("fileId") @PathVariable("fileId") Long fileId
    ) {
        return ResponseEntity.ok(this.snippetService.deleteFile(fileId, id));
    }
}
