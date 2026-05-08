package com.hackathon.api.domain.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

        @Size(max = 50)
        String nickname,

        @Size(max = 150)
        String bio,

        String profileImage,

        @Size(max = 50)
        String name,

        LocalDate birthday,

        @Min(0) @Max(150)
        Integer age,

        @Size(max = 50)
        String job
) {
}
