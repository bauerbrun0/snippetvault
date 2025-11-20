package org.bauerbrun0.snippetvault.api.service;

import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTag(Long userId, String name, String color) {
        return this.tagRepository.createTag(userId, name, color);
    }
}
