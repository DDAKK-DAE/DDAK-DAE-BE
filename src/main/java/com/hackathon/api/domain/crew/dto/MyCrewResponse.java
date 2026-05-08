package com.hackathon.api.domain.crew.dto;

import com.hackathon.api.domain.crew.entity.Crew;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * GET /me/crews 응답 항목.
 * lastActivityAt 은 별도 이력 테이블이 없으므로 크루 생성일로 대체한다.
 */
public record MyCrewResponse(
        UUID crewId,
        String challengeTitle,
        int memberCount,
        LocalDateTime lastActivityAt
) {

    public static MyCrewResponse from(Crew crew, long memberCount) {
        return new MyCrewResponse(
                crew.getId(),
                crew.getChallenge().getTitle(),
                (int) memberCount,
                crew.getCreatedAt()
        );
    }
}
