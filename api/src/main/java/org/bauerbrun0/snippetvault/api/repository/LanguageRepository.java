package org.bauerbrun0.snippetvault.api.repository;

import org.bauerbrun0.snippetvault.api.model.Language;

import java.util.List;

public interface LanguageRepository {
    List<Language> getAllLanguages();
}
