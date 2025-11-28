package org.bauerbrun0.snippetvault.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.dto.*;
import org.bauerbrun0.snippetvault.api.model.*;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.security.JwtUtil;
import org.bauerbrun0.snippetvault.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and management")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserService userService,
            JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Login a user",
            description = "Authenticates a user with username and password and returns a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User login credentials",
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = this.userService.getUserByUsername(loginRequest.getUsername());
        List<Role> roles = this.userService.getUserRoles(user.getId());
        boolean isAdmin = roles.stream().anyMatch(role -> role.getName().equals(Role.ADMIN));

        String jwt = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(jwt, user.getId(), user.getUsername(), user.getCreated(), isAdmin));
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user. Only admins are allowed.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = RegisterUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Admin role required")
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        String passwordHash = passwordEncoder.encode(registerUserRequest.getPassword());
        String[] roles = registerUserRequest.isAdmin() ? new String[]{Role.USER, Role.ADMIN} : new String[]{Role.USER};
        User user = this.userService.createUser(registerUserRequest.getUsername(), passwordHash, roles);

        RegisterUserResponse response = new RegisterUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setCreated(user.getCreated());
        response.setAdmin(registerUserRequest.isAdmin());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get current authenticated user",
            description = "Returns the user information based on the JWT of the current session.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> currentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.getUser(userDetails.getId());

        UserResponse response = new UserResponse();
        response.setUsername(userDetails.getUsername());
        response.setCreated(user.getCreated());
        response.setUserId(userDetails.getId());
        response.setAdmin(userDetails.getIsAdmin());

        return ResponseEntity.ok(response);
    }
}
