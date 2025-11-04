package org.bauerbrun0.snippetvault.api.repository;

import org.bauerbrun0.snippetvault.api.exception.DuplicateUsernameException;
import org.bauerbrun0.snippetvault.api.exception.RoleNotFoundException;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.model.User;

import java.util.List;

public interface UserRepository {
    User getUserByUsername(String username) throws UserNotFoundException;
    User getUser(Long id) throws UserNotFoundException;
    List<User> getUsers();
    User deleteUser(Long id) throws UserNotFoundException;
    User createUser(String username, String passwordHash, String[] roles) throws DuplicateUsernameException, RoleNotFoundException;
    User updateUser(Long id, String username, String passwordHash) throws UserNotFoundException, DuplicateUsernameException;
    List<Role> getUserRoles(Long id);
}
