package com.resturant.management.ResturantManagementSystem.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class GenerateKey {
    public static void main(String[] args) {
        // Generate random 256-bit key for HS256
        byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(key);

        System.out.println("Your Base64 secret key: " + base64Key);
    }
}
