package com.resturant.management.ResturantManagementSystem.service;

import com.resturant.management.ResturantManagementSystem.dto.LoginRequest;
import com.resturant.management.ResturantManagementSystem.dto.LoginResponse;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request){
        return userRepository.findByUsernmAndUpass(request.getUsername(),request.getPass())
                .map(user -> new LoginResponse(
                        user.getUsername(),
                        user.getURole(),
                        "Login successful"
                ))
                .orElse(new LoginResponse(null,null,"Invalid username or passowrd"));
    }
}
