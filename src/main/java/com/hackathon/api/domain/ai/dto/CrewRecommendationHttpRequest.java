package com.hackathon.api.domain.ai.dto;

import java.util.List;

public record CrewRecommendationHttpRequest(
        String crewId,
        List<CrewHistoryItem> crewHistory,
        List<TrendingChallenge> trendingChallenges
) {}
