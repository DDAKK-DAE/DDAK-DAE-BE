package com.hackathon.api.domain.ai.dto;

import java.util.List;
import java.util.Map;

public record CrewRecommendationRequest(
        String crewId,
        Map<String, Integer> memberCategoryFrequency,
        List<TrendingChallenge> trendingChallenges
) {}
