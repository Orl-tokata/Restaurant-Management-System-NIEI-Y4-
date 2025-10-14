package com.resturant.management.ResturantManagementSystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String username;
    private String pass;
}
