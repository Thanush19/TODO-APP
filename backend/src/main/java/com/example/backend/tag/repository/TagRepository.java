package com.example.backend.tag.repository;

import com.example.backend.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findAllByUserId(UUID userId);

    Optional<Tag> findByIdAndUserId(UUID tagId, UUID userId);

    boolean existsByUserIdAndName(UUID userId, String name);

    List<Tag> findAllByIdInAndUserId(
            Collection<UUID> tagIds,
            UUID userId
    );
}