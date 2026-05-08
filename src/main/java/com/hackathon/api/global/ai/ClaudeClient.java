package com.hackathon.api.global.ai;

import com.hackathon.api.global.exception.BusinessException;
import com.hackathon.api.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ClaudeClient {

    private final RestClient restClient;
    private final ClaudeProperties props;

    public ClaudeClient(ClaudeProperties props) {
        this.props = props;
        this.restClient = RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("x-api-key", props.apiKey())
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    /**
     * Claude Messages API를 호출해 텍스트 응답을 반환한다.
     */
    public String generate(String systemPrompt, String userPrompt, int maxTokens) {
        var requestBody = Map.of(
                "model", props.model(),
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userPrompt))
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri("/v1/messages")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            @SuppressWarnings("unchecked")
            var contentList = (List<Map<String, Object>>) response.get("content");
            return (String) contentList.get(0).get("text");

        } catch (Exception e) {
            log.error("Claude API 호출 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "AI 호출 실패");
        }
    }
}
