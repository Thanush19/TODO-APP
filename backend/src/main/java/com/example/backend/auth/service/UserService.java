package com.example.backend.auth.service;

import com.example.backend.auth.dto.user.UpdateProfileRequest;
import com.example.backend.auth.dto.user.UserProfileResponse;
import com.example.backend.auth.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {

        User user = getAuthenticatedUser();

        return toUserProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateCurrentUser(UpdateProfileRequest request) {

        User user = getAuthenticatedUser();

        user.updateName(request.getName().trim());

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