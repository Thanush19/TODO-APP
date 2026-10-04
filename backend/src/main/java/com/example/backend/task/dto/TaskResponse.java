package com.example.backend.task.dto;

import com.example.backend.task.entity.Task;
import com.example.backend.task.entity.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private boolean completed;
    private TaskPriority priority;
    private OffsetDateTime dueAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getPriority(),
                task.getDueAt(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}