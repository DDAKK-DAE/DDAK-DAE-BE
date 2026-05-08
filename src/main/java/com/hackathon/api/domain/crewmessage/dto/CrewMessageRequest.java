package com.hackathon.api.domain.crewmessage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrewMessageRequest(
        @NotBlank
        @Size(max = 2000)
        String content
) {
}
