package com.hackathon.api.domain.auth.dto;

public record AuthResponse(String token, UserResponse user) {
}
