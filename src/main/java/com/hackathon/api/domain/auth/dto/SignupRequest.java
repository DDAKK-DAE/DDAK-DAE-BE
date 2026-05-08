package com.hackathon.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SignupRequest(

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotBlank @Size(max = 50)
        String nickname,

        @Size(max = 50)
        String name,

        LocalDate birthday,

        @Min(0) @Max(150)
        Integer age,

        @Size(max = 50)
        String job
) {
}
