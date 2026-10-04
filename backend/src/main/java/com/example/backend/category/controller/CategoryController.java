package com.example.backend.category.controller;

import com.example.backend.auth.entity.User;
import com.example.backend.category.dto.CategoryResponse;
import com.example.backend.category.dto.CreateCategoryRequest;
import com.example.backend.category.dto.UpdateCategoryRequest;
import com.example.backend.category.service.CategoryService;
import com.example.backend.common.dto.MessageResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            Authentication authentication,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        User user = getAuthenticatedUser(authentication);

        CategoryResponse response =
                categoryService.createCategory(user, request);

        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            Authentication authentication
    ) {
        UUID userId = getAuthenticatedUser(authentication).getId();

        return ResponseEntity.ok(
                categoryService.getCategories(userId)
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(
            Authentication authentication,
            @PathVariable UUID categoryId
    ) {
        UUID userId = getAuthenticatedUser(authentication).getId();

        return ResponseEntity.ok(
                categoryService.getCategory(userId, categoryId)
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            Authentication authentication,
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        UUID userId = getAuthenticatedUser(authentication).getId();

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        userId,
                        categoryId,
                        request
                )
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<MessageResponse> deleteCategory(
            Authentication authentication,
            @PathVariable UUID categoryId
    ) {
        UUID userId = getAuthenticatedUser(authentication).getId();

        categoryService.deleteCategory(userId, categoryId);

        return ResponseEntity.ok(
                new MessageResponse("Category deleted successfully")
        );
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}