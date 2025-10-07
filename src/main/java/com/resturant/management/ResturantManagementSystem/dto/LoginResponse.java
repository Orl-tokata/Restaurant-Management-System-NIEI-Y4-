package com.resturant.management.ResturantManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LoginResponse {
    private String name;
    private String role;
    private String message;
}
