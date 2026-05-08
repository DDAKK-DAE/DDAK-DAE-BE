package com.hackathon.api.domain.crew.dto;

import com.hackathon.api.domain.crew.entity.Crew;
import com.hackathon.api.domain.crew.entity.CrewMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** GET /crews/{id} 응답 — 크루 정보 + 챌린지 요약 + 멤버 목록 */
public record CrewDetailResponse(
        UUID id,
        ChallengeInfo challenge,
        List<MemberInfo> members,
        LocalDateTime createdAt
) {

    /** 크루 상세에 포함할 챌린지 요약 정보 */
    public record ChallengeInfo(
            UUID id,
            String title,
            String category,
            String locationText,
            String audioUrl,
            LocalDateTime deadlineAt
    ) {
    }

    public record MemberInfo(
            UUID userId,
            String nickname,
            String profileImage
    ) {
    }

    public static CrewDetailResponse from(Crew crew, List<CrewMember> members) {
        ChallengeInfo challengeInfo = new ChallengeInfo(
                crew.getChallenge().getId(),
                crew.getChallenge().getTitle(),
                crew.getChallenge().getCategory(),
                crew.getChallenge().getLocationText(),
                crew.getChallenge().getAudioUrl(),
                crew.getChallenge().getDeadlineAt()
        );
        List<MemberInfo> memberInfos = members.stream()
                .map(m -> new MemberInfo(
                        m.getUser().getId(),
                        m.getUser().getNickname(),
                        m.getUser().getProfileImage()))
                .toList();
        return new CrewDetailResponse(crew.getId(), challengeInfo, memberInfos, crew.getCreatedAt());
    }
}
