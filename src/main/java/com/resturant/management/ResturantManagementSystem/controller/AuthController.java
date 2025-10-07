package com.resturant.management.ResturantManagementSystem.controller;

import com.resturant.management.ResturantManagementSystem.dto.LoginRequest;
import com.resturant.management.ResturantManagementSystem.dto.LoginResponse;
import com.resturant.management.ResturantManagementSystem.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow Angular access
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}
