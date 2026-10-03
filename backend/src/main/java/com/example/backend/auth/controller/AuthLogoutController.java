package com.example.backend.auth.controller;

import com.example.backend.auth.dto.RefreshTokenRequest;
import com.example.backend.auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthLogoutController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        refreshTokenService.revokeByRawToken(
                request.getRefreshToken()
        );

        return ResponseEntity.noContent().build();
    }
}