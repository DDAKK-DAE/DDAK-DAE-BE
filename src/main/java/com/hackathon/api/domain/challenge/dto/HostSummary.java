package com.hackathon.api.domain.challenge.dto;

import com.hackathon.api.domain.user.entity.User;

import java.util.UUID;

public record HostSummary(
        UUID id,
        String nickname,
        String profileImage
) {
    public static HostSummary from(User user) {
        return new HostSummary(user.getId(), user.getNickname(), user.getProfileImage());
    }
}
