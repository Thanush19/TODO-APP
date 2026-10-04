package com.example.backend.task.controller;

import com.example.backend.auth.entity.User;
import com.example.backend.common.dto.MessageResponse;
import com.example.backend.task.dto.CreateTaskRequest;
import com.example.backend.task.dto.TaskResponse;
import com.example.backend.task.dto.UpdateTaskCompletionRequest;
import com.example.backend.task.dto.UpdateTaskRequest;
import com.example.backend.task.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            Authentication authentication,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        UUID userId = getAuthenticatedUserId(authentication);

        TaskResponse response = taskService.createTask(
                userId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            Authentication authentication
    ) {
        UUID userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                taskService.getTasks(userId)
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            Authentication authentication,
            @PathVariable UUID taskId
    ) {
        UUID userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                taskService.getTask(userId, taskId)
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        UUID userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                taskService.updateTask(
                        userId,
                        taskId,
                        request
                )
        );
    }

    @PatchMapping("/{taskId}/completion")
    public ResponseEntity<TaskResponse> updateCompletion(
            Authentication authentication,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskCompletionRequest request
    ) {
        UUID userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                taskService.updateCompletion(
                        userId,
                        taskId,
                        request
                )
        );
    }
    @DeleteMapping("/{taskId}")
    public ResponseEntity<MessageResponse> deleteTask(
            Authentication authentication,
            @PathVariable UUID taskId
    ) {
        UUID userId = getAuthenticatedUserId(authentication);

        taskService.deleteTask(userId, taskId);

        return ResponseEntity.ok(
                new MessageResponse("Task deleted successfully")
        );
    }

    private UUID getAuthenticatedUserId(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return user.getId();
    }
}