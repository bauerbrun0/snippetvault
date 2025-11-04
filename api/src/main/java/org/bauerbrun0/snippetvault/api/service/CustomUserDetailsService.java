package org.bauerbrun0.snippetvault.api.service;

import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.exception.UserNotFoundException;
import org.bauerbrun0.snippetvault.api.exception.UserRepositoryException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.model.User;
import org.bauerbrun0.snippetvault.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = this.userRepository.getUserByUsername(username);
            List<Role> roles = this.userRepository.getUserRoles(user.getId());

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPasswordHash())
                    .roles(roles.stream().map(Role::getName).toArray(String[]::new))
                    .build();
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        } catch (UserRepositoryException e) {
            log.error("Repository error while loading user '{}': {}", username, e.getMessage(), e);
            throw new RuntimeException("Internal server error while loading user", e);
        }
    }
}
