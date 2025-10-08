package com.resturant.management.ResturantManagementSystem.controller;

import com.resturant.management.ResturantManagementSystem.util.DateTimeUtil;
import com.resturant.management.ResturantManagementSystem.dto.*;
import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import com.resturant.management.ResturantManagementSystem.security.JwtService;
import com.resturant.management.ResturantManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    // --------------------- REGISTER ---------------------
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with userId, userNm, userPwd, eml, and tel")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {

        try {
            // Validate if userId or email already exists
            if (userService.existsByUserId(request.getUserId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(400, "User ID already exists", null));
            }

            if (userService.existsByEmail(request.getEml())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(400, "Email already exists", null));
            }

            // Create user
            UserInfm user = userService.registerUser(request, passwordEncoder);

            // Generate JWT token
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully",
                    new AuthResponse(token, "Registration successful")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
        }
    }

    // --------------------- LOGIN ---------------------
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user with userId and userPwd")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) {
        try {
            // First check if user exists
            var userOptional = userRepository.findByUserId(request.getUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(401).body(
                        new ApiResponse<>(401, "The User is not found!", null)
                );
            }

            var user = userOptional.get();

            // Check if account is locked
            if (user.isLocked()) {
                return ResponseEntity.status(423).body(
                        new ApiResponse<>(423, "Account is locked due to too many failed login attempts. Please try again later.", null)
                );
            }

            // Try to authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserId(),
                            request.getUserPwd()
                    )
            );

            // Successful login: reset attempts and update last login time
            user.resetFailedLoginAttempts();
            user.setLstLgnDtm(DateTimeUtil.formatDefault(LocalDateTime.now()));
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return ResponseEntity.ok(new ApiResponse<>(200, "Login successful",
                    AuthResponse.builder().token(jwtToken).message("Login successful").build()));

        } catch (BadCredentialsException ex) {
            // Handle incorrect password
            userRepository.findByUserId(request.getUserId()).ifPresent(user -> {
                user.incrementFailedLoginAttempts();
                userRepository.save(user);
            });
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, "Incorrect userId or password", null)
            );
        } catch (UsernameNotFoundException ex) {
            // Handle user not found
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, "Incorrect userId or password", null)
            );
        } catch (AuthenticationException ex) {
            // Handle other authentication errors
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, "Authentication failed", null)
            );
        } catch (Exception ex) {
            // Log the exception for debugging
            System.err.println("Login error: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(500).body(
                    new ApiResponse<>(500, "Internal server error", null)
            );
        }
    }

    // --------------------- Logout ---------------------

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the current user")
    public ResponseEntity<ApiResponse<AuthResponse>> logout() {
        return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful",
                AuthResponse.builder().message("Logout successful").build()));
    }

    // --------------------- FORGOT PASSWORD ---------------------
    @GetMapping("/forgot-pass/{username}")
    public ResponseEntity<?> forgotPassword(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok("Your password: " + user.getUserPwd()))
                .orElseGet(() -> ResponseEntity.badRequest().body("Username not found!"));
    }

    // --------------------- CHANGE PASSWORD ---------------------
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        boolean updated = userService.changePassword(request.getUsername(),
                request.getOldPassword(), request.getNewPassword());

        if (updated) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid username or old password.");
        }
    }
}

