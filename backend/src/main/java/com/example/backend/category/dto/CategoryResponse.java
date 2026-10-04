package com.example.backend.category.dto;

import com.example.backend.category.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;
    private String name;
    private CategoryType type;
    private Instant createdAt;
    private Instant updatedAt;
}