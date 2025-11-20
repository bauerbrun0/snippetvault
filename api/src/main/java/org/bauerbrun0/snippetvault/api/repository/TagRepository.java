package org.bauerbrun0.snippetvault.api.repository;

import org.bauerbrun0.snippetvault.api.exception.InvalidTagColorException;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.model.Tag;

public interface TagRepository {
    Tag createTag(Long userId, String name, String color) throws UserNotFoundException, InvalidTagColorException;
}
