package com.resturant.management.ResturantManagementSystem.service;


import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userInfmRepo;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserInfm user = userInfmRepo.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId())
                .password(user.getUserPwd()) // Make sure this is already BCrypt encoded
                .roles("USER") // or use user.getRole() if you have roles
                .build();
    }
}
