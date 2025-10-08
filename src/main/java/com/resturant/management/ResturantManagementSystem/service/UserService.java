package com.resturant.management.ResturantManagementSystem.service;

import com.resturant.management.ResturantManagementSystem.entity.User;
import com.resturant.management.ResturantManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<User> getUserByUsername(String username) {
        return repo.findByUsername(username);
    }

    public boolean changePassword(String username,String oldPassword, String newPassword){
        Optional<User> userOtp = repo.findByUsername(username);

        if(userOtp.isPresent()){
            User user = userOtp.get();
            if(user.getUpass().equals(oldPassword)){
                user.setUpass(newPassword);
                repo.save(user);
                return true;
            }
        }
        return false;
    }
}
