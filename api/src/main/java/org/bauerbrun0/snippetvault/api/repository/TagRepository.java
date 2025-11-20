package org.bauerbrun0.snippetvault.api.repository;

import org.bauerbrun0.snippetvault.api.exception.InvalidTagColorException;
import org.bauerbrun0.snippetvault.api.exception.TagNotFoundException;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.model.Tag;

import java.util.List;

public interface TagRepository {
    Tag createTag(Long userId, String name, String color) throws UserNotFoundException, InvalidTagColorException;
    Tag updateTag(Long id, String name, String color) throws TagNotFoundException, InvalidTagColorException;
    Tag deleteTag(Long id) throws TagNotFoundException;
    Tag getTag(Long id) throws TagNotFoundException;
    List<Tag> getTags(Long userId);
}
