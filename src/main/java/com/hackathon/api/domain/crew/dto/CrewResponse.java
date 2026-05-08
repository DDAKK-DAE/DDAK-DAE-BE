package com.hackathon.api.domain.crew.dto;

import java.util.List;
import java.util.UUID;

/**
 * POST /challenges/{id}/close 응답.
 * A 팀(ChallengeService)이 CrewService.createCrew() 호출 후 이 객체를 클라이언트에 반환한다.
 */
public record CrewResponse(
        UUID crewId,
        List<UUID> memberIds
) {
}
