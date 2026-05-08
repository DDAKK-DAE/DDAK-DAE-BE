package com.hackathon.api.domain.ai.dto;

import java.util.List;

public record ChemistryRequest(
        String challengeTitle,
        List<MemberProfile> currentMembers,
        MemberProfile applicant
) {}
