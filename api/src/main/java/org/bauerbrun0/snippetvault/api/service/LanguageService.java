package org.bauerbrun0.snippetvault.api.service;


import org.bauerbrun0.snippetvault.api.model.Language;
import org.bauerbrun0.snippetvault.api.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getLanguages() {
        return this.languageRepository.getAllLanguages();
    }
}
