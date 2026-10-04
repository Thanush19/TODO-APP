package com.example.backend.tag.service;

import com.example.backend.auth.entity.User;
import com.example.backend.common.exception.ConflictException;
import com.example.backend.common.exception.EntityNotFoundException;
import com.example.backend.tag.dto.CreateTagRequest;
import com.example.backend.tag.dto.TagResponse;
import com.example.backend.tag.dto.UpdateTagRequest;
import com.example.backend.tag.entity.Tag;
import com.example.backend.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagResponse createTag(
            User user,
            CreateTagRequest request
    ) {
        UUID userId = user.getId();
        String name = request.getName().trim();

        if (tagRepository.existsByUserIdAndName(userId, name)) {
            throw new ConflictException("Tag already exists");
        }

        Tag tag = Tag.builder()
                .user(user)
                .name(name)
                .build();

        Tag savedTag = tagRepository.save(tag);

        return TagResponse.from(savedTag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTags(User user) {

        return tagRepository.findAllByUserId(user.getId())
                .stream()
                .map(TagResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TagResponse getTag(
            User user,
            UUID tagId
    ) {
        Tag tag = getOwnedTag(user.getId(), tagId);

        return TagResponse.from(tag);
    }

    public TagResponse updateTag(
            User user,
            UUID tagId,
            UpdateTagRequest request
    ) {
        Tag tag = getOwnedTag(user.getId(), tagId);

        String name = request.getName().trim();

        if (!tag.getName().equals(name)
                && tagRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new ConflictException("Tag already exists");
        }

        tag.updateName(name);

        return TagResponse.from(tag);
    }

    public void deleteTag(
            User user,
            UUID tagId
    ) {
        Tag tag = getOwnedTag(user.getId(), tagId);

        tagRepository.delete(tag);
    }

    private Tag getOwnedTag(
            UUID userId,
            UUID tagId
    ) {
        return tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Tag not found")
                );
    }
}