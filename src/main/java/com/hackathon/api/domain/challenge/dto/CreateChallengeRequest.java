package com.hackathon.api.domain.challenge.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record CreateChallengeRequest(
        @NotBlank String title,
        String description,
        @NotBlank String locationText,
        @NotBlank String category,
        @NotNull @Min(2) @Max(6) Integer maxParticipants,
        @NotNull @Future LocalDateTime deadlineAt,
        String audioUrl,
        List<String> hashtags
) {}
