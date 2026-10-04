package com.example.backend.tag.controller;

import com.example.backend.auth.entity.User;
import com.example.backend.common.dto.MessageResponse;
import com.example.backend.tag.dto.CreateTagRequest;
import com.example.backend.tag.dto.TagResponse;
import com.example.backend.tag.dto.UpdateTagRequest;
import com.example.backend.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            Authentication authentication,
            @Valid @RequestBody CreateTagRequest request
    ) {

        User user = (User) authentication.getPrincipal();

        TagResponse response = tagService.createTag(user, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getTags(
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                tagService.getTags(user)
        );
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponse> getTag(
            Authentication authentication,
            @PathVariable UUID tagId
    ) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                tagService.getTag(user, tagId)
        );
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponse> updateTag(
            Authentication authentication,
            @PathVariable UUID tagId,
            @Valid @RequestBody UpdateTagRequest request
    ) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                tagService.updateTag(user, tagId, request)
        );
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<MessageResponse> deleteTag(
            Authentication authentication,
            @PathVariable UUID tagId
    ) {

        User user = (User) authentication.getPrincipal();

        tagService.deleteTag(user, tagId);

        return ResponseEntity.ok(
                new MessageResponse("Tag deleted successfully")
        );
    }
}