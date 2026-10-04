package com.example.backend.category.entity;

import com.example.backend.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_categories_user")
    )
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Category(
            User user,
            String name,
            CategoryType type
    ) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static Category createCustom(
            User user,
            String name
    ) {
        return new Category(
                user,
                name,
                CategoryType.CUSTOM
        );
    }

    public static Category createSystem(
            String name
    ) {
        return new Category(
                null,
                name,
                CategoryType.SYSTEM
        );
    }

    public void updateName(String name) {
        if (type == CategoryType.SYSTEM) {
            throw new IllegalStateException(
                    "System categories cannot be modified"
            );
        }

        this.name = name;
        this.updatedAt = Instant.now();
    }
}