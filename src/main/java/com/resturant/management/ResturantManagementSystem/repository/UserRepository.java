package com.resturant.management.ResturantManagementSystem.repository;

import com.resturant.management.ResturantManagementSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernmAndUpass(String username,String upass);
}
