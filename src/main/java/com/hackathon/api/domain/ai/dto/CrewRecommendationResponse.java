package com.hackathon.api.domain.ai.dto;

import java.util.List;

public record CrewRecommendationResponse(List<RecommendationItem> recommendations) {}
