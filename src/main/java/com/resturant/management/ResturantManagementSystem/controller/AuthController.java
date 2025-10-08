package com.resturant.management.ResturantManagementSystem.controller;

import com.resturant.management.ResturantManagementSystem.dto.ChangePasswordRequest;
import com.resturant.management.ResturantManagementSystem.dto.LoginRequest;
import com.resturant.management.ResturantManagementSystem.dto.LoginResponse;
import com.resturant.management.ResturantManagementSystem.entity.User;
import com.resturant.management.ResturantManagementSystem.service.AuthService;
import com.resturant.management.ResturantManagementSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow Angular access
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }


    //Forgot Password API
    @GetMapping("/forgot-pass/{username}")
    public ResponseEntity<?> forgotPassword(@PathVariable String username){
        Optional<User> user = userService.getUserByUsername(username);
        if(user.isPresent()){
            return ResponseEntity.ok(user.get().getUpass());
        }else{
            return ResponseEntity.badRequest().body("Username not found!");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){
        boolean success = userService.changePassword(request.getUsername(),
                                                     request.getOldPassword(),
                                                     request.getNewPassword());
        if(success){
            return ResponseEntity.ok("Password has been updated successfully.");
        }else {
            return ResponseEntity.badRequest().body("Invalid username or old password. ");
        }
    }
}
