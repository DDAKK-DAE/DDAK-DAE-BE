package com.hackathon.api.domain.challenge.dto;

import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChallengeListItemResponse(
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
        List<String> hashtags
) {
    public static ChallengeListItemResponse of(Challenge challenge, User host) {
        return new ChallengeListItemResponse(
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
                challenge.getHashtags()
        );
    }
}
