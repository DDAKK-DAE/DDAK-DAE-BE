package com.hackathon.api.domain.reel.controller;

import com.hackathon.api.domain.reel.dto.ChallengeReelFeedResponse;
import com.hackathon.api.domain.reel.dto.ReelResponse;
import com.hackathon.api.domain.reel.dto.ReelUploadRequest;
import com.hackathon.api.domain.reel.service.ReelService;
import com.hackathon.api.global.response.ApiResponse;
import com.hackathon.api.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 릴스 컨트롤러.
 * POST /reels          — 파일 업로드 후 받은 URL을 videoUrl로 전달해 릴스를 등록한다.
 * GET  /challenges/{id}/reels — 같은 챌린지의 릴스를 음원 통일 몰아보기 피드로 제공한다.
 */
@Tag(name = "Reels", description = "릴스 업로드 및 피드 조회")
@RestController
@RequiredArgsConstructor
public class ReelController {

    private final ReelService reelService;

    @Operation(summary = "릴스 업로드",
            description = "crewId 없으면 모집용(recruitment), 있으면 완료용(completion). 완료용은 크루원 전원 자동 등록.")
    @PostMapping("/reels")
    public ApiResponse<ReelResponse> upload(
            @RequestBody @Valid ReelUploadRequest request,
            @CurrentUserId UUID userId) {
        return ApiResponse.ok(reelService.upload(userId, request));
    }

    @Operation(summary = "챌린지별 릴스 피드",
            description = "type=recruitment|completion 필터 가능. 챌린지 audio_url 포함해 음원 통일 재생 지원.")
    @GetMapping("/challenges/{challengeId}/reels")
    public ApiResponse<ChallengeReelFeedResponse> getChallengeReels(
            @PathVariable UUID challengeId,
            @RequestParam(required = false) String type) {
        return ApiResponse.ok(reelService.getChallengeReels(challengeId, type));
    }

    @Operation(summary = "유저 릴스 이력 조회", description = "reel_participants 기반으로 해당 유저가 참여한 릴스 목록을 반환합니다.")
    @GetMapping("/users/{userId}/reels")
    public ApiResponse<List<ReelResponse>> getUserReels(@PathVariable UUID userId) {
        return ApiResponse.ok(reelService.getUserReels(userId));
    }
}
