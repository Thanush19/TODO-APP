package com.example.backend.auth.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 100)
    private String name;

    private String currentPassword;

    @Size(min = 8, max = 100)
    private String newPassword;
}