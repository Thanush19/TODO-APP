package com.example.backend.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskCompletionRequest {

    @NotNull(message = "Completed is required")
    private Boolean completed;
}