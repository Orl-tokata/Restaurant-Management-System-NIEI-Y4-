package com.resturant.management.ResturantManagementSystem.controller;

import com.resturant.management.ResturantManagementSystem.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class SendMailController {
    private final MailService mailService;

    @GetMapping("/send-mail")
    public String sendMail() {
        mailService.send("target_email@gmail.com", "Test Email", "Hello, this is a test!");
        return "Mail sent successfully!";
    }
}
