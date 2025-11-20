package org.bauerbrun0.snippetvault.api;

import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.config.AppProperties;
import org.bauerbrun0.snippetvault.api.exception.DuplicateUsernameException;
import org.bauerbrun0.snippetvault.api.model.Role;
import org.bauerbrun0.snippetvault.api.model.Snippet;
import org.bauerbrun0.snippetvault.api.model.Tag;
import org.bauerbrun0.snippetvault.api.repository.SnippetRepository;
import org.bauerbrun0.snippetvault.api.repository.UserRepository;
import org.bauerbrun0.snippetvault.api.service.SnippetService;
import org.bauerbrun0.snippetvault.api.service.TagService;
import org.bauerbrun0.snippetvault.api.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class SnippetvaultApiApplication implements CommandLineRunner {

    private final UserService userService;
    private final SnippetService snippetService;
    private final TagService tagService;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public SnippetvaultApiApplication(
            UserService userService, SnippetService snippetService, TagService tagService, PasswordEncoder passwordEncoder, AppProperties appProperties
    ) {
        this.userService = userService;
        this.snippetService = snippetService;
        this.tagService = tagService;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    public static void main(String[] args) {
		SpringApplication.run(SnippetvaultApiApplication.class, args);
	}

    @Override
    public void run(String... args) {
        this.createDefaultUser();
        this.createDummyData();
    }

    private void createDefaultUser() {
        try {
            String username = this.appProperties.getAdminUser();
            String password = this.appProperties.getAdminPassword();
            this.userService.createUser(username, passwordEncoder.encode(password), new String[]{Role.USER, Role.ADMIN});
            log.info("Created default user {}", username);
        } catch (DuplicateUsernameException e) {
            log.info("Default user already created");
        }
    }

    // TODO: later delete, just for development
    private void createDummyData() {
        Snippet snippet = this.snippetService.create(1L, "First Snippet", "ASDASD");
        log.info("Created snippet {}", snippet);

        Tag tag = this.tagService.createTag(1L, "First Tag", "#ffffff");
        log.info("Created tag {}", tag);

        this.snippetService.addTagToSnippet(snippet.getId(), tag.getId());
        log.info("Added tag {} to snippet {}", tag.getId(), snippet.getId());
    }
}
