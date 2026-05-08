package com.hackathon.api.domain.reel.dto;

import java.util.List;

/** GET /challenges/{id}/reels 응답 — 챌린지 음원 + 릴스 목록을 같이 내려 프론트에서 통일 재생 */
public record ChallengeReelFeedResponse(
        String audioUrl,
        List<ReelResponse> reels
) {
}
