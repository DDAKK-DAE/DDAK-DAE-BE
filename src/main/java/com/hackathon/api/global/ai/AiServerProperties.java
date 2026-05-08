package com.hackathon.api.global.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.ai-server")
public record AiServerProperties(
        @DefaultValue("http://localhost:8000") String baseUrl,
        @DefaultValue("") String internalKey
) {
    public AiServerProperties {
        boolean isLocal = baseUrl != null &&
                (baseUrl.startsWith("http://localhost") || baseUrl.startsWith("http://127.0.0.1"));
        if (!isLocal && !StringUtils.hasText(internalKey)) {
            throw new IllegalArgumentException(
                    "app.ai-server.internal-key must be configured for non-local AI server");
        }
    }
}
