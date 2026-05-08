package com.hackathon.api.domain.challenge.dto;

import java.util.List;
import java.util.UUID;

public record CloseChallengeResponse(
        UUID crewId,
        List<UUID> memberIds
) {}
