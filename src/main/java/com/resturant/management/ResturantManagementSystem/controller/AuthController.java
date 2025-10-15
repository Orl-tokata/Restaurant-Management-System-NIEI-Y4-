package com.resturant.management.ResturantManagementSystem.controller;

import com.resturant.management.ResturantManagementSystem.dto.request.RegisterRequest;
import com.resturant.management.ResturantManagementSystem.dto.response.ApiMsgResp;
import com.resturant.management.ResturantManagementSystem.dto.response.ApiResponse;
import com.resturant.management.ResturantManagementSystem.dto.response.AuthResponse;
import com.resturant.management.ResturantManagementSystem.enums.YesNo;
import com.resturant.management.ResturantManagementSystem.service.ForgotPasswordService;
import com.resturant.management.ResturantManagementSystem.service.JwtService;
import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import com.resturant.management.ResturantManagementSystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;


@RestController
@RequestMapping("restaurant_mng/auth/")
@Tag(name = "Authentication", description = "Serve for login and logout")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ForgotPasswordService forgotPasswordService;

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
    @Operation(summary = "Serve login", description = "Your UserId can be ID or email or phone number")
    @PostMapping("login")
    public ResponseEntity<ApiMsgResp<?>> login(
            @RequestParam String userId,
            @RequestParam String userPwd,
            HttpServletRequest httpRequest) {

        try {
            Optional<UserInfm> userOpt = userRepository.findByUserId(userId);

            if (userOpt.isEmpty()) {
                log.warn("Login attempt with non-existent user ID: {}", userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiMsgResp<>(HttpStatus.UNAUTHORIZED.value(), "Your ID doesn't exist"));
            }

            UserInfm user = userOpt.get(); // unwrap Optional

            int loginCount = Integer.parseInt(Optional.ofNullable(user.getLoginFailedCnt()).orElse("0"));

            // Account locked
            if (loginCount >= 5) {
                user.setLockYn(YesNo.YES.getValue());
                userRepository.save(user);
                log.warn("Account locked due to too many failed login attempts: {}", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiMsgResp<>(HttpStatus.FORBIDDEN.value(), "Too many failed login attempts. Your account has been locked!"));
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, userPwd)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Reset login failed count on successful login
            user.setLoginFailedCnt("0");
            userRepository.save(user);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            log.info("Login successful for user ID: {}", userId);
            return ResponseEntity.ok().body(new ApiMsgResp<>(token, HttpStatus.OK.value(), "Login successful"));

        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiMsgResp<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
        } catch (Exception e) {
            log.error("An error occurred during login for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiMsgResp<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred. Please try again later."));
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
    @GetMapping("/forgot-pass/{userId}")
    @Operation(summary = "Forgot password", description = "Retrieve user password by userId (for demo only — do not expose in production)")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@PathVariable String userId) {
        try {
            return userService.getUserByUserId(userId)
                    .map(user -> ResponseEntity.ok(
                            new ApiResponse<>(200, "User found", "Your password: " + user.getUserPwd())
                    ))
                    .orElseGet(() -> ResponseEntity.status(404)
                            .body(new ApiResponse<>(404, "User ID not found", null))
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
        }
    }

    // --------------------- RESET PASSWORD ---------------------
    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset password using token from email")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            forgotPasswordService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new ApiResponse<>(200, "Password reset successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }


    // --------------------- CHANGE PASSWORD ---------------------
    /*@PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password by userId")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            boolean updated = userService.changePassword(
                    request.getUserId(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            if (updated) {
                return ResponseEntity.ok(
                        new ApiResponse<>(200, "Password updated successfully", null)
                );
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<>(400, "Invalid userId or old password", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
        }
    }*/

}

