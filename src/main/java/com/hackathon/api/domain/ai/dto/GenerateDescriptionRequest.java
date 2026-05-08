package com.hackathon.api.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateDescriptionRequest(
        String title,
        String locationText,
        Integer maxParticipants,
        String category,
        String descriptionHint,
        String mood,
        String targetAudience,
        String difficulty
) {}
