package org.bauerbrun0.snippetvault.api.controller;


import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.model.UpdateUserRequest;
import org.bauerbrun0.snippetvault.api.model.User;
import org.bauerbrun0.snippetvault.api.model.UserResponse;
import org.bauerbrun0.snippetvault.api.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public List<UserResponse> getUsers() {
        List<User> users = userService.getUsers();

        List<UserResponse> response = new ArrayList<>();
        for (User user : users) {
            boolean isAdmin = this.userService.isUserAdmin(user.getId());

            UserResponse ur  = new UserResponse();
            ur.setUserId(user.getId());
            ur.setUsername(user.getUsername());
            ur.setCreated(user.getCreated());
            ur.setAdmin(isAdmin);
            response.add(ur);
        }
        return response;
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") Long id) {
        User user = this.userService.getUser(id);
        return getUserResponse(user);
    }

    @DeleteMapping("/{id}")
    public UserResponse deleteUser(@PathVariable("id") Long id) {
        boolean isAdmin = this.userService.isUserAdmin(id);
        User user = this.userService.deleteUser(id);

        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setCreated(user.getCreated());
        response.setAdmin(isAdmin);
        return response;
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        boolean isAdmin = this.userService.isUserAdmin(id);

        String username = updateUserRequest.getUsername();
        String password = updateUserRequest.getPassword();

        if (password != null && !password.isEmpty()) {
            password = passwordEncoder.encode(password);
        }

        User user = this.userService.updateUser(id, username, password);

        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setCreated(user.getCreated());
        response.setAdmin(isAdmin);
        return response;
    }

    @GetMapping("/{username}/by-username")
    public UserResponse getUser(@PathVariable("username") String username) {
        User user = this.userService.getUserByUsername(username);
        return getUserResponse(user);
    }

    private UserResponse getUserResponse(User user) {
        boolean isAdmin = this.userService.isUserAdmin(user.getId());

        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setCreated(user.getCreated());
        response.setAdmin(isAdmin);
        return response;
    }
}
