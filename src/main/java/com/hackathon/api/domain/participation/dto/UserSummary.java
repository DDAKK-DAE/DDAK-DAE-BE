package com.hackathon.api.domain.participation.dto;

import com.hackathon.api.domain.user.entity.User;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String nickname,
        String profileImage
) {
    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getNickname(), user.getProfileImage());
    }
}
