package com.example.backend.category;

import com.example.backend.auth.entity.User;
import com.example.backend.category.dto.CategoryResponse;
import com.example.backend.category.dto.CreateCategoryRequest;
import com.example.backend.category.dto.UpdateCategoryRequest;
import com.example.backend.category.entity.Category;
import com.example.backend.category.entity.CategoryType;
import com.example.backend.category.repository.CategoryRepository;
import com.example.backend.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.springframework.web.server.ResponseStatusException;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    private User user;
    private User anotherUser;

    private UUID userId;
    private UUID anotherUserId;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);

        userId = UUID.randomUUID();
        anotherUserId = UUID.randomUUID();

        user = mock(User.class);
        anotherUser = mock(User.class);

        when(user.getId()).thenReturn(userId);
        when(anotherUser.getId()).thenReturn(anotherUserId);
    }

    @Test
    void createCategory_shouldCreateCustomCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("  Projects  ");

        Category category = mockCategory(
                UUID.randomUUID(),
                "Projects",
                CategoryType.CUSTOM,
                userId
        );

        when(categoryRepository.existsByUserIdAndName(
                userId,
                "Projects"
        )).thenReturn(false);

        when(categoryRepository.existsByNameAndType(
                "Projects",
                CategoryType.SYSTEM
        )).thenReturn(false);

        try (MockedStatic<Category> mockedStatic =
                     mockStatic(Category.class)) {

            mockedStatic
                    .when(() -> Category.createCustom(user, "Projects"))
                    .thenReturn(category);

            when(categoryRepository.save(category))
                    .thenReturn(category);

            CategoryResponse response =
                    categoryService.createCategory(user, request);

            assertThat(response.getName()).isEqualTo("Projects");
            assertThat(response.getType())
                    .isEqualTo(CategoryType.CUSTOM);

            verify(categoryRepository).save(category);
        }
    }

    @Test
    void createCategory_shouldRejectDuplicateCustomName() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Work");

        when(categoryRepository.existsByUserIdAndName(
                userId,
                "Work"
        )).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.createCategory(user, request)
                );

        assertThat(exception.getStatusCode().value())
                .isEqualTo(409);

        verify(categoryRepository, never())
                .save(any());
    }

    @Test
    void createCategory_shouldRejectSystemCategoryName() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Work");

        when(categoryRepository.existsByUserIdAndName(
                userId,
                "Work"
        )).thenReturn(false);

        when(categoryRepository.existsByNameAndType(
                "Work",
                CategoryType.SYSTEM
        )).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.createCategory(user, request)
                );

        assertThat(exception.getStatusCode().value())
                .isEqualTo(409);

        verify(categoryRepository, never())
                .save(any());
    }

    @Test
    void getCategories_shouldReturnSystemAndUserCategories() {
        Category systemCategory = mockCategory(
                UUID.randomUUID(),
                "Work",
                CategoryType.SYSTEM,
                null
        );

        Category customCategory = mockCategory(
                UUID.randomUUID(),
                "Interview Prep",
                CategoryType.CUSTOM,
                userId
        );

        when(categoryRepository.findAllByUserIdOrTypeOrderByNameAsc(
                userId,
                CategoryType.SYSTEM
        )).thenReturn(List.of(
                customCategory,
                systemCategory
        ));

        List<CategoryResponse> response =
                categoryService.getCategories(userId);

        assertThat(response).hasSize(2);

        assertThat(response)
                .extracting(CategoryResponse::getName)
                .containsExactly(
                        "Interview Prep",
                        "Work"
                );
    }

    @Test
    void getCategory_shouldAllowSystemCategory() {
        UUID categoryId = UUID.randomUUID();

        Category systemCategory = mockCategory(
                categoryId,
                "Work",
                CategoryType.SYSTEM,
                null
        );

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.of(systemCategory));

        CategoryResponse response =
                categoryService.getCategory(userId, categoryId);

        assertThat(response.getName()).isEqualTo("Work");
        assertThat(response.getType())
                .isEqualTo(CategoryType.SYSTEM);
    }

    @Test
    void getCategory_shouldAllowOwnCustomCategory() {
        UUID categoryId = UUID.randomUUID();

        Category customCategory = mockCategory(
                categoryId,
                "Interview Prep",
                CategoryType.CUSTOM,
                userId
        );

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndUserId(
                categoryId,
                userId
        )).thenReturn(Optional.of(customCategory));

        CategoryResponse response =
                categoryService.getCategory(userId, categoryId);

        assertThat(response.getName())
                .isEqualTo("Interview Prep");
        assertThat(response.getType())
                .isEqualTo(CategoryType.CUSTOM);
    }

    @Test
    void getCategory_shouldRejectAnotherUsersCustomCategory() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndUserId(
                categoryId,
                userId
        )).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.getCategory(
                                userId,
                                categoryId
                        )
                );

        assertThat(exception.getStatusCode().value())
                .isEqualTo(404);
    }

    @Test
    void updateCategory_shouldRejectSystemCategory() {
        UUID categoryId = UUID.randomUUID();

        Category systemCategory = mockCategory(
                categoryId,
                "Work",
                CategoryType.SYSTEM,
                null
        );

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.of(systemCategory));

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Office");

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.updateCategory(
                                userId,
                                categoryId,
                                request
                        )
                );

        assertThat(exception.getStatusCode().value())
                .isEqualTo(403);

        verify(systemCategory, never())
                .updateName(anyString());
    }

    @Test
    void updateCategory_shouldUpdateOwnCustomCategory() {
        UUID categoryId = UUID.randomUUID();

        Category customCategory = mockCategory(
                categoryId,
                "Interview",
                CategoryType.CUSTOM,
                userId
        );

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndUserId(
                categoryId,
                userId
        )).thenReturn(Optional.of(customCategory));

        when(categoryRepository.existsByUserIdAndName(
                userId,
                "Interview Prep"
        )).thenReturn(false);

        when(categoryRepository.existsByNameAndType(
                "Interview Prep",
                CategoryType.SYSTEM
        )).thenReturn(false);

        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("  Interview Prep  ");

        CategoryResponse response =
                categoryService.updateCategory(
                        userId,
                        categoryId,
                        request
                );

        assertThat(response.getName())
                .isEqualTo("Interview");

        verify(customCategory)
                .updateName("Interview Prep");
    }

    @Test
    void deleteCategory_shouldRejectSystemCategory() {
        UUID categoryId = UUID.randomUUID();

        Category systemCategory = mockCategory(
                categoryId,
                "Work",
                CategoryType.SYSTEM,
                null
        );

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.of(systemCategory));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> categoryService.deleteCategory(
                                userId,
                                categoryId
                        )
                );

        assertThat(exception.getStatusCode().value())
                .isEqualTo(403);

        verify(categoryRepository, never())
                .delete(any());
    }

    @Test
    void deleteCategory_shouldDeleteOwnCustomCategory() {
        UUID categoryId = UUID.randomUUID();

        Category customCategory = mockCategory(
                categoryId,
                "Interview Prep",
                CategoryType.CUSTOM,
                userId
        );

        when(categoryRepository.findByIdAndType(
                categoryId,
                CategoryType.SYSTEM
        )).thenReturn(Optional.empty());

        when(categoryRepository.findByIdAndUserId(
                categoryId,
                userId
        )).thenReturn(Optional.of(customCategory));

        categoryService.deleteCategory(userId, categoryId);

        verify(categoryRepository)
                .delete(customCategory);
    }

    private Category mockCategory(
            UUID id,
            String name,
            CategoryType type,
            UUID ownerId
    ) {
        Category category = mock(Category.class);

        when(category.getId()).thenReturn(id);
        when(category.getName()).thenReturn(name);
        when(category.getType()).thenReturn(type);
        when(category.getCreatedAt()).thenReturn(Instant.now());
        when(category.getUpdatedAt()).thenReturn(Instant.now());

        if (ownerId != null) {
            User owner = mock(User.class);
            when(owner.getId()).thenReturn(ownerId);
            when(category.getUser()).thenReturn(owner);
        }

        return category;
    }
}