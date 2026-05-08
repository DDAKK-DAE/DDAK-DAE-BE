package com.hackathon.api.domain.auth.dto;

import com.hackathon.api.domain.user.entity.User;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String nickname,
        String bio,
        String profileImage,
        String name,
        LocalDate birthday,
        Short age,
        String job
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getBio(),
                user.getProfileImage(),
                user.getName(),
                user.getBirthday(),
                user.getAge(),
                user.getJob()
        );
    }
}
