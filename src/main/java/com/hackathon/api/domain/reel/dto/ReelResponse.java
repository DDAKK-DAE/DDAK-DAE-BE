package com.hackathon.api.domain.reel.dto;

import com.hackathon.api.domain.reel.entity.Reel;
import com.hackathon.api.domain.reel.entity.ReelParticipant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** 릴스 단건 응답 — 업로드 결과 및 피드 목록 양쪽에서 공통으로 사용 */
public record ReelResponse(
        UUID id,
        UUID challengeId,
        UUID crewId,          // 모집용(recruitment)이면 null
        String videoUrl,
        String reelType,      // "recruitment" | "completion"
        List<ParticipantInfo> participants,
        LocalDateTime createdAt
) {

    /** 릴스 참여자 요약 — 크루원 카드 및 피드에서 참여자 프로필 표시용 */
    public record ParticipantInfo(
            UUID userId,
            String nickname,
            String profileImage
    ) {
    }

    public static ReelResponse from(Reel reel, List<ReelParticipant> participants) {
        List<ParticipantInfo> participantInfos = participants.stream()
                .map(p -> new ParticipantInfo(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        p.getUser().getProfileImage()))
                .toList();

        return new ReelResponse(
                reel.getId(),
                reel.getChallenge().getId(),
                reel.getCrew() != null ? reel.getCrew().getId() : null,
                reel.getVideoUrl(),
                reel.getReelType(),
                participantInfos,
                reel.getCreatedAt()
        );
    }
}
