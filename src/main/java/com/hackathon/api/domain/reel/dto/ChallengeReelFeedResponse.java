package com.hackathon.api.domain.reel.dto;

import java.util.List;
import java.util.UUID;

/**
 * GET /challenges/{id}/reels 응답.
 * audioUrl — 프론트에서 음원 통일 재생에 사용.
 * crewId   — 챌린지가 마감되어 크루가 생성된 경우 해당 크루 ID, 아직 모집 중이면 null.
 */
public record ChallengeReelFeedResponse(
        String audioUrl,
        UUID crewId,
        List<ReelResponse> reels
) {
}
