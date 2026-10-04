package com.example.backend.category.service;

import com.example.backend.auth.entity.User;
import com.example.backend.category.dto.CategoryResponse;
import com.example.backend.category.dto.CreateCategoryRequest;
import com.example.backend.category.dto.UpdateCategoryRequest;
import com.example.backend.category.entity.Category;
import com.example.backend.category.entity.CategoryType;
import com.example.backend.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(
            User user,
            CreateCategoryRequest request
    ) {
        String name = request.getName().trim();

        if (categoryRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category with this name already exists"
            );
        }

        if (categoryRepository.existsByNameAndType(
                name,
                CategoryType.SYSTEM
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A system category with this name already exists"
            );
        }

        Category category = Category.createCustom(user, name);

        categoryRepository.save(category);

        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(UUID userId) {

        return categoryRepository
                .findAllByUserIdOrTypeOrderByNameAsc(
                        userId,
                        CategoryType.SYSTEM
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(
            UUID userId,
            UUID categoryId
    ) {
        Category category = findAccessibleCategory(
                userId,
                categoryId
        );

        return toResponse(category);
    }

    public CategoryResponse updateCategory(
            UUID userId,
            UUID categoryId,
            UpdateCategoryRequest request
    ) {
        Category category = findAccessibleCategory(
                userId,
                categoryId
        );

        if (category.getType() == CategoryType.SYSTEM) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "System categories cannot be modified"
            );
        }

        String name = request.getName().trim();

        if (!category.getName().equals(name)
                && categoryRepository.existsByUserIdAndName(
                userId,
                name
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category with this name already exists"
            );
        }

        if (categoryRepository.existsByNameAndType(
                name,
                CategoryType.SYSTEM
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A system category with this name already exists"
            );
        }

        category.updateName(name);

        return toResponse(category);
    }

    public void deleteCategory(
            UUID userId,
            UUID categoryId
    ) {
        Category category = findAccessibleCategory(
                userId,
                categoryId
        );

        if (category.getType() == CategoryType.SYSTEM) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "System categories cannot be deleted"
            );
        }

        categoryRepository.delete(category);
    }

    private Category findAccessibleCategory(
            UUID userId,
            UUID categoryId
    ) {
        Optional<Category> systemCategory =
                categoryRepository.findByIdAndType(
                        categoryId,
                        CategoryType.SYSTEM
                );

        if (systemCategory.isPresent()) {
            return systemCategory.get();
        }

        return categoryRepository
                .findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                ));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}