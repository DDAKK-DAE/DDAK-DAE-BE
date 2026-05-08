package com.hackathon.api.domain.reel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReelUploadRequest(
        @NotNull UUID challengeId,
        UUID crewId,
        @NotBlank String videoUrl
) {
}
