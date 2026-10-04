package com.example.backend.task.repository;

import com.example.backend.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);

    List<Task> findAllByParentTaskIdAndUserIdOrderByCreatedAtAsc(
            UUID parentTaskId,
            UUID userId
    );

    boolean existsByIdAndParentTaskId(
            UUID taskId,
            UUID parentTaskId
    );
}