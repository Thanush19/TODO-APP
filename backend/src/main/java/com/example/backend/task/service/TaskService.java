package com.example.backend.task.service;

import com.example.backend.auth.entity.User;
import com.example.backend.auth.repository.UserRepository;
import com.example.backend.category.entity.Category;
import com.example.backend.category.entity.CategoryType;
import com.example.backend.category.repository.CategoryRepository;
import com.example.backend.tag.entity.Tag;
import com.example.backend.tag.repository.TagRepository;
import com.example.backend.task.dto.CreateTaskRequest;
import com.example.backend.task.dto.TaskResponse;
import com.example.backend.task.dto.UpdateTaskCompletionRequest;
import com.example.backend.task.dto.UpdateTaskRequest;
import com.example.backend.task.entity.Task;
import com.example.backend.task.entity.TaskPriority;
import com.example.backend.task.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public TaskResponse createTask(
            UUID userId,
            CreateTaskRequest request
    ) {
        User user = getUser(userId);

        Category category = getAccessibleCategory(
                userId,
                request.getCategoryId()
        );

        Set<Tag> tags = getAccessibleTags(
                userId,
                request.getTagIds()
        );

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .user(user)
                .category(category)
                .tags(tags)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .completed(false)
                .priority(
                        request.getPriority() != null
                                ? request.getPriority()
                                : TaskPriority.MEDIUM
                )
                .dueAt(request.getDueAt())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(UUID userId) {

        return taskRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(
            UUID userId,
            UUID taskId
    ) {
        Task task = getUserTask(userId, taskId);

        return TaskResponse.from(task);
    }

    public TaskResponse updateTask(
            UUID userId,
            UUID taskId,
            UpdateTaskRequest request
    ) {
        Task task = getUserTask(userId, taskId);

        Category category = getAccessibleCategory(
                userId,
                request.getCategoryId()
        );

        Set<Tag> tags = getAccessibleTags(
                userId,
                request.getTagIds()
        );

        task.update(
                request.getTitle().trim(),
                request.getDescription(),
                request.getPriority() != null
                        ? request.getPriority()
                        : TaskPriority.MEDIUM,
                request.getDueAt(),
                category
        );

        task.updateTags(tags);

        return TaskResponse.from(task);
    }

    public TaskResponse updateCompletion(
            UUID userId,
            UUID taskId,
            UpdateTaskCompletionRequest request
    ) {
        Task task = getUserTask(userId, taskId);

        task.updateCompletion(request.getCompleted());

        return TaskResponse.from(task);
    }

    public void deleteTask(
            UUID userId,
            UUID taskId
    ) {
        Task task = getUserTask(userId, taskId);

        taskRepository.delete(task);
    }

    private Category getAccessibleCategory(
            UUID userId,
            UUID categoryId
    ) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository
                .findByIdAndType(categoryId, CategoryType.SYSTEM)
                .orElseGet(() ->
                        categoryRepository
                                .findByIdAndUserId(categoryId, userId)
                                .orElseThrow(() ->
                                        new EntityNotFoundException(
                                                "Category not found"
                                        )
                                )
                );
    }

    private Set<Tag> getAccessibleTags(
            UUID userId,
            Set<UUID> tagIds
    ) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Tag> tags = tagRepository.findAllByIdInAndUserId(
                tagIds,
                userId
        );

        if (tags.size() != tagIds.size()) {
            throw new EntityNotFoundException("Tag not found");
        }

        return new HashSet<>(tags);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found")
                );
    }

    private Task getUserTask(
            UUID userId,
            UUID taskId
    ) {
        return taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Task not found")
                );
    }
}