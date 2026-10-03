package com.example.backend.auth.service;

import com.example.backend.auth.entity.RefreshToken;
import com.example.backend.auth.entity.User;
import com.example.backend.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .tokenHash(hashToken(rawToken))
                .expiresAt(
                        OffsetDateTime.now()
                                .plusNanos(refreshTokenExpirationMs * 1_000_000)
                )
                .createdAt(OffsetDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RefreshToken validate(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid refresh token")
                );

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {

        refreshToken.setRevokedAt(OffsetDateTime.now());

        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    @Transactional
    public void revokeByRawToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(this::revoke);
    }
}