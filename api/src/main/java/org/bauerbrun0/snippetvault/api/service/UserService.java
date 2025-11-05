package org.bauerbrun0.snippetvault.api.service;

import org.bauerbrun0.snippetvault.api.exception.DuplicateUsernameException;
import org.bauerbrun0.snippetvault.api.exception.RoleNotFoundException;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.model.User;
import org.bauerbrun0.snippetvault.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return this.userRepository.getUserByUsername(username);
    }

    public User getUser(Long id) throws UserNotFoundException {
        return this.userRepository.getUser(id);
    }

    public List<User> getUsers() {
        return this.userRepository.getUsers();
    }

    public User deleteUser(Long id) throws UserNotFoundException {
        return this.userRepository.deleteUser(id);
    }

    public User createUser(String username, String passwordHash, String[] roles ) throws DuplicateUsernameException, RoleNotFoundException {
        return this.userRepository.createUser(username, passwordHash, roles);
    }

    public User  updateUser(Long id, String username, String passwordHash) throws UserNotFoundException, DuplicateUsernameException {
        return this.userRepository.updateUser(id, username, passwordHash);
    }

    public List<Role> getUserRoles(Long id) {
        return this.userRepository.getUserRoles(id);
    }
}
