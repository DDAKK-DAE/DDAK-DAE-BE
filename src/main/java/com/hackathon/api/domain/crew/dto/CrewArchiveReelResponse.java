package com.hackathon.api.domain.crew.dto;

import com.hackathon.api.domain.reel.entity.Reel;
import com.hackathon.api.domain.reel.entity.ReelParticipant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * GET /crews/{id}/archive 응답 항목.
 * 완료 릴스(reel_type='completion') 목록과 각 릴스의 공동 참여자를 포함한다.
 */
public record CrewArchiveReelResponse(
        UUID id,
        String videoUrl,
        List<ParticipantInfo> participants,
        LocalDateTime createdAt
) {

    public record ParticipantInfo(
            UUID userId,
            String nickname,
            String profileImage
    ) {
    }

    public static CrewArchiveReelResponse from(Reel reel, List<ReelParticipant> participants) {
        List<ParticipantInfo> participantInfos = participants.stream()
                .map(p -> new ParticipantInfo(
                        p.getUser().getId(),
                        p.getUser().getNickname(),
                        p.getUser().getProfileImage()))
                .toList();
        return new CrewArchiveReelResponse(reel.getId(), reel.getVideoUrl(), participantInfos, reel.getCreatedAt());
    }
}
