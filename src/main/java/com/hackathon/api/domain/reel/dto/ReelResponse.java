package com.hackathon.api.domain.reel.dto;

import com.hackathon.api.domain.reel.entity.Reel;
import com.hackathon.api.domain.reel.entity.ReelParticipant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReelResponse(
        UUID id,
        UUID challengeId,
        UUID crewId,
        String videoUrl,
        String reelType,
        List<ParticipantInfo> participants,
        LocalDateTime createdAt
) {

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
