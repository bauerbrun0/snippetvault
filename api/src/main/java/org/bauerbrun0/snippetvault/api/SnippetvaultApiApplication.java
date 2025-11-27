package org.bauerbrun0.snippetvault.api;

import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.config.AppProperties;
import org.bauerbrun0.snippetvault.api.exception.DuplicateUsernameException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class SnippetvaultApiApplication implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public SnippetvaultApiApplication(
            UserService userService, PasswordEncoder passwordEncoder, AppProperties appProperties
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    public static void main(String[] args) {
		SpringApplication.run(SnippetvaultApiApplication.class, args);
	}

    @Override
    public void run(String... args) {
        log.info("Frontend URL: {}", this.appProperties.getFrontend());
        this.createDefaultUser();
    }

    private void createDefaultUser() {
        try {
            String username = this.appProperties.getAdminuser();
            String password = this.appProperties.getAdminpassword();
            this.userService.createUser(username, passwordEncoder.encode(password), new String[]{Role.USER, Role.ADMIN});
            log.info("Created default user {}", username);
        } catch (DuplicateUsernameException e) {
            log.info("Default user already created");
        }
    }

}
