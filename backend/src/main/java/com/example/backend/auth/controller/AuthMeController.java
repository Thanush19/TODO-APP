package com.example.backend.auth.controller;

import com.example.backend.auth.dto.LoginResponse;
import com.example.backend.auth.entity.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthMeController {

    @GetMapping("/me")
    public LoginResponse.UserInfo getCurrentUser(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return LoginResponse.UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}