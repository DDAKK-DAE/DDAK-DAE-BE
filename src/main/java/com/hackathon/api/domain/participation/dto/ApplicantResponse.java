package com.hackathon.api.domain.participation.dto;

import com.hackathon.api.domain.participation.entity.Participation;
import com.hackathon.api.domain.user.entity.User;

import java.util.UUID;

public record ApplicantResponse(
        UUID participationId,
        UserSummary user,
        String introMessage,
        String status
) {
    public static ApplicantResponse of(Participation participation, User user) {
        return new ApplicantResponse(
                participation.getId(),
                UserSummary.from(user),
                participation.getIntroMessage(),
                participation.getStatus()
        );
    }
}
