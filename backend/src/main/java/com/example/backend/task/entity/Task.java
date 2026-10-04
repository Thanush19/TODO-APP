package com.example.backend.task.entity;

import com.example.backend.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(
        name = "tasks",
        indexes = {
                @Index(name = "idx_tasks_user_id", columnList = "user_id"),
                @Index(
                        name = "idx_tasks_user_id_created_at",
                        columnList = "user_id, created_at"
                )
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tasks_user")
    )
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void update(
            String title,
            String description,
            TaskPriority priority,
            OffsetDateTime dueAt
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueAt = dueAt;
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void updateCompletion(boolean completed) {
        this.completed = completed;
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}