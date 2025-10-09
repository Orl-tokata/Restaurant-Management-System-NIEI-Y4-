package com.resturant.management.ResturantManagementSystem.service;

import com.resturant.management.ResturantManagementSystem.entity.PasswordResetToken;
import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import com.resturant.management.ResturantManagementSystem.repository.PasswordResetTokenRepository;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    // Step 1: Request password reset
    public void createPasswordResetToken(String email) {
        Optional<UserInfm> optionalUser = userRepository.findByEml(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("No user found with this email.");
        }

        UserInfm user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // valid for 15 minutes
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        mailService.send(email, "Password Reset Request",
                "Click the following link to reset your password: " + resetLink);
    }

    // Step 2: Reset the password
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        UserInfm user = resetToken.getUser();
        user.setUserPwd(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken); // prevent reuse
    }
}

