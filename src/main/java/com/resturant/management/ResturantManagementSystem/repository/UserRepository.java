package com.resturant.management.ResturantManagementSystem.repository;

import com.resturant.management.ResturantManagementSystem.entity.UserInfm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfm, String> {
    Optional<UserInfm> findByUserId(String userId);
    Optional<UserInfm> findByEml(String email);
}


