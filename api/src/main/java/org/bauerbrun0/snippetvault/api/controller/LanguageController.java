package org.bauerbrun0.snippetvault.api.controller;


import org.bauerbrun0.snippetvault.api.model.Language;
import org.bauerbrun0.snippetvault.api.service.LanguageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping("")
    public List<Language> getLanguages() {
        return this.languageService.getLanguages();
    }
}
