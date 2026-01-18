package pse.nebula.user.controller;

import pse.nebula.user.dto.AuthenticationResult;
import pse.nebula.user.model.User;
import pse.nebula.user.service.UserService;
import pse.nebula.user.service.TokenBlacklistService;
import pse.nebula.user.dto.LoginRequest;
import pse.nebula.user.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Register a new user
     * POST /users/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Login user and get JWT token
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthenticationResult authResult = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            User user = authResult.getUser();

            LoginResponse response = new LoginResponse();
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());
            response.setToken(authResult.getToken());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Logout user and blacklist token
     * POST /users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            // Extract token from "Bearer <token>" header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Add token to blacklist
            tokenBlacklistService.blacklistToken(token);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Check if a token is blacklisted
     * GET /users/blacklist/check
     * Header: X-Token-Check (contains the token to check)
     */
    @GetMapping("/blacklist/check")
    public ResponseEntity<Boolean> checkTokenBlacklist(@RequestHeader("X-Token-Check") String token) {
        try {
            boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);
            return ResponseEntity.ok(isBlacklisted);
        } catch (Exception e) {
            log.error("Error checking token blacklist", e);
            // Return false on error to avoid blocking legitimate requests
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user profile
     * Users can only update their own profile unless they are admins
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        // Get authenticated user ID from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // The principal is set as String userId in JwtAuthenticationFilter
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String authenticatedUserId = (String) principal;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // Check if user is trying to update their own profile or is an admin
        if (!authenticatedUserId.equals(id.toString()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

}