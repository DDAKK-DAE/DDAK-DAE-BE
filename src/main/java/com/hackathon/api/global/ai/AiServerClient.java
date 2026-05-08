package com.hackathon.api.global.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.api.domain.ai.dto.ChemistryRequest;
import com.hackathon.api.domain.ai.dto.ChemistryResponse;
import com.hackathon.api.domain.ai.dto.CrewRecommendationRequest;
import com.hackathon.api.domain.ai.dto.CrewRecommendationResponse;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionRequest;
import com.hackathon.api.domain.ai.dto.GenerateDescriptionResponse;
import com.hackathon.api.global.exception.BusinessException;
import com.hackathon.api.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class AiServerClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String internalKey;

    public AiServerClient(AiServerProperties props) {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = props.baseUrl();
        this.internalKey = props.internalKey();
    }

    private <T> T post(String path, Object reqBody, Class<T> responseClass, String errorMsg) {
        try {
            String json = objectMapper.writeValueAsString(reqBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .header("x-internal-api-key", internalKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error("AI 서버 오류 [{}] {}: {}", response.statusCode(), path, response.body());
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, errorMsg);
            }
            return objectMapper.readValue(response.body(), responseClass);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 서버 호출 실패 {}: {}", path, e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, errorMsg);
        }
    }

    public GenerateDescriptionResponse generateDescription(GenerateDescriptionRequest req) {
        return post("/ai/generate-description", req, GenerateDescriptionResponse.class, "AI 글 생성 실패");
    }

    public ChemistryResponse chemistry(ChemistryRequest req) {
        return post("/ai/chemistry", req, ChemistryResponse.class, "케미 분석 실패");
    }

    public CrewRecommendationResponse crewRecommendation(CrewRecommendationRequest req) {
        return post("/ai/crew-recommendation", req, CrewRecommendationResponse.class, "크루 챌린지 추천 실패");
    }
}
