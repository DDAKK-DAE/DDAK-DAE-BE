package com.hackathon.api.domain.crew.controller;

import com.hackathon.api.domain.crew.dto.CrewArchiveReelResponse;
import com.hackathon.api.domain.crew.dto.CrewDetailResponse;
import com.hackathon.api.domain.crew.dto.MyCrewResponse;
import com.hackathon.api.domain.crew.service.CrewService;
import com.hackathon.api.global.response.ApiResponse;
import com.hackathon.api.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Crews", description = "크루 조회 / 아카이브 / 내 크루 목록")
@RestController
@RequiredArgsConstructor
public class CrewController {

    private final CrewService crewService;

    /**
     * 크루 상세 조회 — 크루 멤버만 접근 가능.
     * 챌린지 요약 + 멤버 목록을 함께 반환한다.
     */
    @Operation(summary = "크루 상세 조회", description = "크루 멤버만 접근 가능. 챌린지 정보와 멤버 목록 포함.")
    @GetMapping("/crews/{id}")
    public ApiResponse<CrewDetailResponse> getCrewDetail(
            @PathVariable UUID id,
            @CurrentUserId UUID userId) {
        return ApiResponse.ok(crewService.getCrewDetail(id, userId));
    }

    /**
     * 크루 릴스 아카이브 — 해당 크루의 완료 릴스 목록 반환.
     * 각 릴스에는 공동 참여자 정보가 포함된다.
     */
    @Operation(summary = "크루 릴스 아카이브", description = "완료 릴스 목록과 공동 참여자를 반환. 크루 멤버만 접근 가능.")
    @GetMapping("/crews/{id}/archive")
    public ApiResponse<List<CrewArchiveReelResponse>> getCrewArchive(
            @PathVariable UUID id,
            @CurrentUserId UUID userId) {
        return ApiResponse.ok(crewService.getCrewArchive(id, userId));
    }

    /** 내가 속한 크루 목록 조회 */
    @Operation(summary = "내 크루 목록", description = "로그인 유저가 멤버로 속한 크루 목록을 반환.")
    @GetMapping("/me/crews")
    public ApiResponse<List<MyCrewResponse>> getMyCrews(@CurrentUserId UUID userId) {
        return ApiResponse.ok(crewService.getMyCrews(userId));
    }
}
