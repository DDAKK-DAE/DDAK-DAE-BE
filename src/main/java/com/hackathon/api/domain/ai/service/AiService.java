package com.hackathon.api.domain.ai.service;

import com.hackathon.api.domain.ai.dto.ChemistryRequest;
import com.hackathon.api.domain.ai.dto.ChemistryResponse;
import com.hackathon.api.domain.ai.dto.CrewRecommendationRequest;
import com.hackathon.api.domain.ai.dto.CrewRecommendationResponse;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionRequest;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionResponse;
import com.hackathon.api.global.ai.AiServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public CrewRecommendationResponse crewRecommendation(CrewRecommendationRequest req) {
        return aiServerClient.crewRecommendation(req);
    }
}
