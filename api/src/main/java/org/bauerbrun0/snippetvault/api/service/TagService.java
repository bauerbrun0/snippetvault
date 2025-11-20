package org.bauerbrun0.snippetvault.api.service;

import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public Tag updateTag(Long id, String name, String color) {
        return this.tagRepository.updateTag(id, name, color);
    }

    @Transactional
    public Tag deleteTag(Long id) {
        return this.tagRepository.deleteTag(id);
    }

    @Transactional(readOnly = true)
    public Tag getTag(Long id) {
        return this.tagRepository.getTag(id);
    }

    @Transactional(readOnly = true)
    public List<Tag> getTags(Long userId) {
        return this.tagRepository.getTags(userId);
    }
}
