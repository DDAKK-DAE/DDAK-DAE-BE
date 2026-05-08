package com.hackathon.api.domain.crewmessage.dto;

import com.hackathon.api.domain.crewmessage.entity.CrewMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public record CrewMessageResponse(
        UUID id,
        SenderInfo sender,
        String content,
        LocalDateTime createdAt
) {

    public record SenderInfo(
            UUID userId,
            String nickname,
            String profileImage
    ) {
    }

    public static CrewMessageResponse from(CrewMessage message) {
        return new CrewMessageResponse(
                message.getId(),
                new SenderInfo(
                        message.getSender().getId(),
                        message.getSender().getNickname(),
                        message.getSender().getProfileImage()),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
