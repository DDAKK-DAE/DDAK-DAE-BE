package com.hackathon.api.domain.ai.service;

import com.hackathon.api.domain.ai.dto.ChemistryRequest;
import com.hackathon.api.domain.ai.dto.ChemistryResponse;
import com.hackathon.api.domain.ai.dto.CrewHistoryItem;
import com.hackathon.api.domain.ai.dto.CrewRecommendationHttpRequest;
import com.hackathon.api.domain.ai.dto.CrewRecommendationRequest;
import com.hackathon.api.domain.ai.dto.CrewRecommendationResponse;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionRequest;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionResponse;
import com.hackathon.api.global.ai.AiServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiServerClient aiServerClient;

    public GenerateDescriptionResponse generateDescription(GenerateDescriptionRequest req) {
        return aiServerClient.generateDescription(req);
    }

    public ChemistryResponse chemistry(ChemistryRequest req) {
        return aiServerClient.chemistry(req);
    }

    public CrewRecommendationResponse crewRecommendation(CrewRecommendationHttpRequest req) {
        List<CrewHistoryItem> history = req.crewHistory() != null ? req.crewHistory() : List.of();
        Map<String, Integer> frequency = history.stream()
                .filter(item -> item.category() != null)
                .collect(Collectors.groupingBy(CrewHistoryItem::category, Collectors.summingInt(e -> 1)));

        CrewRecommendationRequest fastApiReq = new CrewRecommendationRequest(
                req.crewId(), frequency, req.trendingChallenges()
        );
        return aiServerClient.crewRecommendation(fastApiReq);
    }
}
