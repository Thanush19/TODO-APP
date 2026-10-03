package com.example.backend.auth.controller;

import com.example.backend.auth.dto.RefreshTokenRequest;
import com.example.backend.auth.entity.RefreshToken;
import com.example.backend.auth.entity.User;
import com.example.backend.auth.repository.UserRepository;
import com.example.backend.auth.service.RefreshTokenService;
import com.example.backend.auth.dto.LoginResponse;
import com.example.backend.common.security.JwtTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRefreshController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        RefreshToken currentToken =
                refreshTokenService.validate(request.getRefreshToken());

        User user = userRepository.findById(currentToken.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        /*
         * Rotation:
         * revoke old refresh token
         * issue completely new refresh token
         */

        refreshTokenService.revoke(currentToken);

        String accessToken =
                jwtTokenService.generateAccessToken(user.getId(),user.getEmail());

        String newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .tokenType("Bearer")
                        .expiresIn(900000L)
                        .user(
                                LoginResponse.UserInfo.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .build()
                        )
                        .build()
        );
    }
}