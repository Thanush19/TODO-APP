package com.example.backend.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(
                jwtProperties.getAccessTokenExpiration()
        );

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .id(UUID.randomUUID().toString())
                .signWith(signingKey)
                .compact();
    }

    public long getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    /**
     * Validates the JWT signature and standard claims such as expiration.
     *
     * @param token JWT access token without the "Bearer " prefix
     * @return user ID stored in the JWT subject
     * @throws JwtException if the token is invalid or expired
     */
    public UUID validateAndGetUserId(String token) {

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        String subject = claimsJws.getPayload().getSubject();

        if (subject == null || subject.isBlank()) {
            throw new JwtException("JWT subject is missing");
        }

        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new JwtException("JWT subject is not a valid user ID");
        }
    }
}