package com.hackathon.api.domain.auth.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(max = 50)
        String nickname,

        @Size(max = 150)
        String bio,

        String profileImage
) {
}
