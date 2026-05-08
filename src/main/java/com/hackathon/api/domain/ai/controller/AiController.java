package com.hackathon.api.domain.ai.controller;

import com.hackathon.api.domain.ai.dto.ChemistryRequest;
import com.hackathon.api.domain.ai.dto.ChemistryResponse;
import com.hackathon.api.domain.ai.dto.CrewRecommendationRequest;
import com.hackathon.api.domain.ai.dto.CrewRecommendationResponse;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionRequest;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionResponse;
import com.hackathon.api.domain.ai.service.AiService;
import com.hackathon.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate-description")
    public ApiResponse<GenerateDescriptionResponse> generateDescription(@RequestBody GenerateDescriptionRequest req) {
        return ApiResponse.ok(aiService.generateDescription(req));
    }

    @PostMapping("/chemistry")
    public ApiResponse<ChemistryResponse> chemistry(@RequestBody ChemistryRequest req) {
        return ApiResponse.ok(aiService.chemistry(req));
    }

    @PostMapping("/crew-recommendation")
    public ApiResponse<CrewRecommendationResponse> crewRecommendation(@RequestBody CrewRecommendationRequest req) {
        return ApiResponse.ok(aiService.crewRecommendation(req));
    }
}
