package com.example.backend.auth.service;

import com.example.backend.auth.dto.*;
import com.example.backend.auth.entity.User;
import com.example.backend.auth.exception.InvalidCredentialsException;
import com.example.backend.auth.repository.UserRepository;
import com.example.backend.common.exception.EmailAlreadyRegisteredException;
import com.example.backend.common.security.JwtTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(request.getName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Transactional()
    public LoginResponse login(LoginRequest request) {

        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtTokenService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        String refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenService.getAccessTokenExpiration())
                .user(
                        LoginResponse.UserInfo.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .build()
                )
                .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}