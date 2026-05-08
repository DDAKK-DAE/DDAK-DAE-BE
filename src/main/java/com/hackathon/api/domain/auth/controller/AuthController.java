package com.hackathon.api.domain.auth.controller;

import com.hackathon.api.domain.auth.dto.*;
import com.hackathon.api.domain.auth.service.AuthService;
import com.hackathon.api.global.response.ApiResponse;
import com.hackathon.api.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Auth", description = "회원가입 / 로그인 / 내 프로필")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ApiResponse.ok(authService.signup(request));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@CurrentUserId UUID userId) {
        return ApiResponse.ok(authService.getMe(userId));
    }

    @Operation(summary = "내 프로필 수정")
    @PatchMapping("/me")
    public ApiResponse<UserResponse> updateMe(@CurrentUserId UUID userId,
                                               @RequestBody @Valid UpdateProfileRequest request) {
        return ApiResponse.ok(authService.updateMe(userId, request));
    }
}
