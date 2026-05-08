package com.hackathon.api.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrendingChallenge(
        String id,
        String title,
        String category,
        String locationText,
        List<String> hashtags
) {}
