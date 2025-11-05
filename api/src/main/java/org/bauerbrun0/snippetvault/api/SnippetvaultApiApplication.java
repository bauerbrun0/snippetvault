package org.bauerbrun0.snippetvault.api;

import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.exception.DuplicateUsernameException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class SnippetvaultApiApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SnippetvaultApiApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
		SpringApplication.run(SnippetvaultApiApplication.class, args);
	}

    @Override
    public void run(String... args) {
        this.createDefaultUser();
    }

    private void createDefaultUser() {
        try {
            String username = "admin";
            String password = "password";
            this.userRepository.createUser(username, passwordEncoder.encode(password), new String[]{Role.USER, Role.ADMIN});
            log.info("Created default user {}", username);
        } catch (DuplicateUsernameException e) {
            log.info("Default user already created");
        }
    }
}
