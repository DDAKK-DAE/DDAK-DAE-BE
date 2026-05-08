package com.hackathon.api.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberProfile(
        String nickname,
        String bio,
        String introMessage,
        List<ParticipationHistory> participationHistory
) {}
