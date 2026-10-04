package com.example.backend.category.repository;

import com.example.backend.category.entity.Category;
import com.example.backend.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByUserIdOrTypeOrderByNameAsc(
            UUID userId,
            CategoryType type
    );

    Optional<Category> findByIdAndUserId(
            UUID categoryId,
            UUID userId
    );

    Optional<Category> findByIdAndType(
            UUID categoryId,
            CategoryType type
    );

    boolean existsByUserIdAndName(
            UUID userId,
            String name
    );

    boolean existsByNameAndType(
            String name,
            CategoryType type
    );
}