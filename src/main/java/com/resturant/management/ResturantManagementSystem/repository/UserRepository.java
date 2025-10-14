package com.resturant.management.ResturantManagementSystem.repository;

import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfm, Long> {
    UserInfm findByUserId(String userId);
    UserInfm findByEml(String eml);
    UserInfm findByUsername(String userNm);
}

