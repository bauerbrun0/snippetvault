package org.bauerbrun0.snippetvault.api.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.dto.*;
import org.bauerbrun0.snippetvault.api.model.*;
import org.bauerbrun0.snippetvault.api.security.CustomUserDetails;
import org.bauerbrun0.snippetvault.api.security.JwtUtil;
import org.bauerbrun0.snippetvault.api.service.UserService;
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

    @PostMapping("/login")
    public LoginResponse loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = this.userService.getUserByUsername(loginRequest.getUsername());
        List<Role> roles = this.userService.getUserRoles(user.getId());
        boolean isAdmin = roles.stream().anyMatch(role -> role.getName().equals(Role.ADMIN));

        String jwt = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(jwt, user.getId(), user.getUsername(), user.getCreated(), isAdmin);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public RegisterUserResponse registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        String passwordHash = passwordEncoder.encode(registerUserRequest.getPassword());
        String[] roles = registerUserRequest.isAdmin() ? new String[]{Role.USER, Role.ADMIN} : new String[]{Role.USER};
        User user = this.userService.createUser(registerUserRequest.getUsername(), passwordHash, roles);

        RegisterUserResponse response = new RegisterUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setCreated(user.getCreated());
        response.setAdmin(registerUserRequest.isAdmin());
        return response;
    }

    @GetMapping("/current-user")
    public UserResponse currentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.getUser(userDetails.getId());

        UserResponse response = new UserResponse();
        response.setUsername(userDetails.getUsername());
        response.setCreated(user.getCreated());
        response.setUserId(userDetails.getId());
        response.setAdmin(userDetails.getIsAdmin());

        return response;
    }
}
