package com.resturant.management.ResturantManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String userId;
    private String userNm;
    private String userPwd;
    private String eml;
    private String tel;
}
