package com.hackathon.api.global.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.ai-server")
public record AiServerProperties(
        @DefaultValue("http://localhost:8000") String baseUrl,
        @DefaultValue("") String internalKey
) {
}
