package org.bauerbrun0.snippetvault.api.security;

import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.service.TagService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("tagSecurity")
public class TagSecurity {
    private final TagService tagService;

    public TagSecurity(TagService tagService) {
        this.tagService = tagService;
    }

    public boolean isOwner(Authentication authentication, Long tagId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Tag tag = tagService.getTag(tagId);
        if (tag == null) {
            return true;
        }
        return tag.getUserId().equals(userDetails.getId());
    }
}
