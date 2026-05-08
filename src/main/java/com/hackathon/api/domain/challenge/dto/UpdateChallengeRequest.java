package com.hackathon.api.domain.challenge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateChallengeRequest(
        String title,
        String description,
        String locationText,
        String category,
        @Min(2) @Max(6) Integer maxParticipants,
        LocalDateTime deadlineAt,
        String audioUrl,
        List<String> hashtags
) {}
