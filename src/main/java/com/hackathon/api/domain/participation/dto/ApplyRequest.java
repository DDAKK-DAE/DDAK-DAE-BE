package com.hackathon.api.domain.participation.dto;

import jakarta.validation.constraints.Size;

public record ApplyRequest(
        @Size(max = 200) String introMessage
) {}
