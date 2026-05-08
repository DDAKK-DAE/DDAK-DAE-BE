package com.hackathon.api.domain.crewmessage.dto;

import jakarta.validation.constraints.NotBlank;

public record CrewMessageRequest(
        @NotBlank
        String content
) {
}
