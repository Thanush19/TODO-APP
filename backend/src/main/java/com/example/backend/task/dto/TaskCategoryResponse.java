package com.example.backend.task.dto;

import com.example.backend.category.entity.Category;
import com.example.backend.category.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TaskCategoryResponse {

    private UUID id;
    private String name;
    private CategoryType type;

    public static TaskCategoryResponse from(Category category) {
        return new TaskCategoryResponse(
                category.getId(),
                category.getName(),
                category.getType()
        );
    }
}