package com.resturant.management.ResturantManagementSystem.service;

import com.resturant.management.ResturantManagementSystem.dto.request.RegisterRequest;
import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
    }

    public boolean existsByUserId(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEml(email).isPresent();
    }

    public Optional<UserInfm> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public UserInfm registerUser(RegisterRequest request, PasswordEncoder passwordEncoder) {
        UserInfm user = UserInfm.builder()
                .bizKey(generateBizKey())
                .userId(request.getUserId())
                .userNm(request.getUserNm())
                .userPwd(this.passwordEncoder.encode(request.getUserPwd()))
                .eml(request.getEml())
                .tel(request.getTel())
                .usrImg("")
                .lockYn("N")
                .loginFailedCnt("0")
                .actYn("Y")
                .regId(request.getUserId())
                .regDtm(String.valueOf(LocalDateTime.now()))
                .modId(request.getUserId())
                .modDtm(String.valueOf(LocalDateTime.now()))
                .build();
        return userRepository.save(user);
    }

    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        return userRepository.findByUserId(userId)
                .map(user -> {
                    if (passwordEncoder.matches(oldPassword, user.getUserPwd())) {
                        user.setUserPwd(passwordEncoder.encode(newPassword));
                        userRepository.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    private String generateBizKey() {
        long maxKey = userRepository.findAll().stream()
                .mapToLong(u -> {
                    try {
                        return Long.parseLong(u.getBizKey());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
        return String.format("%05d", maxKey + 1);
    }
}

