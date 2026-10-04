package com.example.backend.tag.dto;

import com.example.backend.tag.entity.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class TagResponse {

    private UUID id;
    private String name;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}