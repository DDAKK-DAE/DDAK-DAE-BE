package com.hackathon.api.global.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.claude")
public record ClaudeProperties(String apiKey, String model, String baseUrl) {
}
