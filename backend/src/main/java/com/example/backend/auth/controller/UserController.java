package com.example.backend.auth.controller;

import com.example.backend.auth.dto.user.UpdateProfileRequest;
import com.example.backend.auth.dto.user.UserProfileResponse;
import com.example.backend.auth.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {

        return ResponseEntity.ok(
                userService.getCurrentUser()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateCurrentUser(request)
        );
    }
}