package com.example.backend.auth.service;

import com.example.backend.auth.dto.user.UpdateProfileRequest;
import com.example.backend.auth.dto.user.UserProfileResponse;
import com.example.backend.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {

        User user = getAuthenticatedUser();

        return toUserProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateCurrentUser(UpdateProfileRequest request) {

        User user = getAuthenticatedUser();

        // Update name if provided
        if (request.getName() != null) {
            String name = request.getName().trim();

            if (name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be blank");
            }

            user.updateName(name);
        }

        // Update password if requested
        if (request.getNewPassword() != null) {

            if (request.getCurrentPassword() == null ||
                    request.getCurrentPassword().isBlank()) {
                throw new IllegalArgumentException(
                        "Current password is required"
                );
            }

            if (!passwordEncoder.matches(
                    request.getCurrentPassword(),
                    user.getPasswordHash()
            )) {
                throw new BadCredentialsException(
                        "Current password is incorrect"
                );
            }

            if (passwordEncoder.matches(
                    request.getNewPassword(),
                    user.getPasswordHash()
            )) {
                throw new IllegalArgumentException(
                        "New password must be different from the current password"
                );
            }

            user.updatePassword(
                    passwordEncoder.encode(request.getNewPassword())
            );
        }

        return toUserProfileResponse(user);
    }
    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new IllegalStateException("Authenticated user is unavailable");
        }

        return user;
    }

    private UserProfileResponse toUserProfileResponse(User user) {

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}