package org.bauerbrun0.snippetvault.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.bauerbrun0.snippetvault.api.dto.UpdateUserRequest;
import org.bauerbrun0.snippetvault.api.model.User;
import org.bauerbrun0.snippetvault.api.dto.UserResponse;
import org.bauerbrun0.snippetvault.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Admin-only user management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Get all users",
            description = "Returns all registered users. Only accessible to admins."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
    )
    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getUsers() {
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
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns a user by ID. Admin-only."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        User user = this.userService.getUser(id);
        return ResponseEntity.ok(getUserResponse(user));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by ID. Admin-only."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User deleted",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable("id") Long id) {
        boolean isAdmin = this.userService.isUserAdmin(id);
        User user = this.userService.deleteUser(id);

        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setCreated(user.getCreated());
        response.setAdmin(isAdmin);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update a user",
            description = "Updates username and/or password. Admin-only."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User updated",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid update request")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
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
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get user by username",
            description = "Returns a user by their username. Admin-only."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{username}/by-username")
    public ResponseEntity<UserResponse> getUser(@PathVariable("username") String username) {
        User user = this.userService.getUserByUsername(username);
        return ResponseEntity.ok(getUserResponse(user));
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
