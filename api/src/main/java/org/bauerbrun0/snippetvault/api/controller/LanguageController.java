package org.bauerbrun0.snippetvault.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bauerbrun0.snippetvault.api.model.Language;
import org.bauerbrun0.snippetvault.api.service.LanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@Tag(name = "Languages", description = "Available programming languages for snippets")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @Operation(
            summary = "Get all languages",
            description = "Returns a list of all supported languages.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of languages",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = Language.class))
            )
    )
    @GetMapping("")
    public ResponseEntity<List<Language>> getLanguages() {
        List<Language> languages = this.languageService.getLanguages();
        return ResponseEntity.ok(languages);
    }
}
