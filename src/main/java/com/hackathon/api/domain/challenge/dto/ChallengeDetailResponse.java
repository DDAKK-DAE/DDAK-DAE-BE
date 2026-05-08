package com.hackathon.api.domain.challenge.dto;

import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChallengeDetailResponse(
        UUID id,
        String title,
        String description,
        String locationText,
        String category,
        String audioUrl,
        int maxParticipants,
        int currentParticipants,
        String status,
        HostSummary host,
        LocalDateTime deadlineAt,
        LocalDateTime createdAt,
        List<String> hashtags,
        List<Object> recruitmentReels,  // B 파트 완료 후 타입 교체
        List<Object> completionReels    // B 파트 완료 후 타입 교체
) {
    public static ChallengeDetailResponse of(Challenge challenge, User host) {
        return new ChallengeDetailResponse(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getLocationText(),
                challenge.getCategory(),
                challenge.getAudioUrl(),
                challenge.getMaxParticipants(),
                challenge.getCurrentParticipants(),
                challenge.getStatus(),
                HostSummary.from(host),
                challenge.getDeadlineAt(),
                challenge.getCreatedAt(),
                challenge.getHashtags(),
                List.of(),
                List.of()
        );
    }
}
