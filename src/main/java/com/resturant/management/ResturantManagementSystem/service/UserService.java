package com.resturant.management.ResturantManagementSystem.service;

import com.resturant.management.ResturantManagementSystem.dto.RegisterRequest;
import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import com.resturant.management.ResturantManagementSystem.model.Role;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByUserId(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEml(email).isPresent();
    }

    public UserInfm registerUser(RegisterRequest request, PasswordEncoder passwordEncoder) {
        UserInfm user = UserInfm.builder()
                .bizKey(generateBizKey())
                .userId(request.getUserId())
                .userNm(request.getUserNm())
                .userPwd(passwordEncoder.encode(request.getUserPwd()))
                .eml(request.getEml())
                .tel(request.getTel())
                .usrImg("")
                .role(Role.USER)
                .lockYn("N")
                .loginFailedCnt(0)
                .actYn("Y")
                .regId(request.getUserId())
                .regDtm(String.valueOf(LocalDateTime.now()))
                .modId(request.getUserId())
                .modDtm(String.valueOf(LocalDateTime.now()))
                .build();
        return userRepository.save(user);
    }

    public Optional<UserInfm> getUserByUsername(String username) {
        return userRepository.findByUserId(username);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<UserInfm> userOpt = userRepository.findByUserId(username);
        if (userOpt.isPresent()) {
            UserInfm user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getUserPwd())) {
                user.setUserPwd(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    private String generateBizKey() {
        var allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) return "00001";

        long maxKey = allUsers.stream()
                .mapToLong(user -> {
                    try { return Long.parseLong(user.getBizKey()); }
                    catch (NumberFormatException e) { return 0; }
                })
                .max().orElse(0);

        return String.format("%05d", maxKey + 1);
    }
}
