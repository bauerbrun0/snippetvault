package org.bauerbrun0.snippetvault.api.service;

import org.bauerbrun0.snippetvault.api.exception.DuplicateUsernameException;
import org.bauerbrun0.snippetvault.api.exception.RoleNotFoundException;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.model.User;
import org.bauerbrun0.snippetvault.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) throws UserNotFoundException {
        return this.userRepository.getUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) throws UserNotFoundException {
        return this.userRepository.getUser(id);
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return this.userRepository.getUsers();
    }

    @Transactional
    public User deleteUser(Long id) throws UserNotFoundException {
        return this.userRepository.deleteUser(id);
    }

    @Transactional
    public User createUser(String username, String passwordHash, String[] roles )
            throws DuplicateUsernameException, RoleNotFoundException {
        return this.userRepository.createUser(username, passwordHash, roles);
    }

    @Transactional
    public User updateUser(Long id, String username, String passwordHash)
            throws UserNotFoundException, DuplicateUsernameException {
        return this.userRepository.updateUser(id, username, passwordHash);
    }

    @Transactional(readOnly = true)
    public List<Role> getUserRoles(Long id) {
        return this.userRepository.getUserRoles(id);
    }

    @Transactional(readOnly = true)
    public boolean isUserAdmin(Long id) {
        List<Role> roles = this.getUserRoles(id);
        return roles.stream().anyMatch(role -> role.getName().equals(Role.ADMIN));
    }
}
