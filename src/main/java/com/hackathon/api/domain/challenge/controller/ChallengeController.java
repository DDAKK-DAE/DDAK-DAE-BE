package com.hackathon.api.domain.challenge.controller;

import com.hackathon.api.domain.challenge.dto.*;
import com.hackathon.api.domain.challenge.service.ChallengeService;
import com.hackathon.api.domain.participation.dto.ApplicantResponse;
import com.hackathon.api.domain.participation.dto.ApplyRequest;
import com.hackathon.api.domain.participation.service.ParticipationService;
import com.hackathon.api.global.response.ApiResponse;
import com.hackathon.api.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Challenges", description = "챌린지 개설 / 피드 / 참여 신청 관리")
@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ParticipationService participationService;

    @Operation(summary = "챌린지 피드 목록 (개인화 정렬)",
               description = "로그인 유저의 카테고리 참여 빈도 기반으로 정렬된 open 챌린지 목록을 반환합니다.")
    @GetMapping
    public ApiResponse<Page<ChallengeListItemResponse>> getFeed(
            @CurrentUserId UUID userId,
            @Parameter(description = "카테고리 필터 (댄스/일상/스포츠/푸드/기타)") @RequestParam(required = false) String category,
            @Parameter(description = "지역 텍스트 필터") @RequestParam(required = false) String location,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(challengeService.getFeed(userId, category, location, pageable));
    }

    @Operation(summary = "챌린지 상세 조회",
               description = "챌린지 정보 + 주최자 프로필 + 릴스 목록을 반환합니다.")
    @GetMapping("/{id}")
    public ApiResponse<ChallengeDetailResponse> getDetail(@PathVariable UUID id) {
        return ApiResponse.ok(challengeService.getDetail(id));
    }

    @Operation(summary = "챌린지 개설",
               description = "새 챌린지 모임을 개설합니다. category: 댄스/일상/스포츠/푸드/기타")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChallengeDetailResponse> create(
            @CurrentUserId UUID userId,
            @RequestBody @Valid CreateChallengeRequest request) {
        return ApiResponse.ok(challengeService.create(userId, request));
    }

    @Operation(summary = "챌린지 수정 (주최자만)",
               description = "챌린지 정보를 부분 수정합니다. null 필드는 변경되지 않습니다.")
    @PatchMapping("/{id}")
    public ApiResponse<ChallengeDetailResponse> update(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody @Valid UpdateChallengeRequest request) {
        return ApiResponse.ok(challengeService.update(userId, id, request));
    }

    @Operation(summary = "챌린지 삭제 (주최자, open 상태만)",
               description = "모집이 시작되지 않은(open) 챌린지만 삭제할 수 있습니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@CurrentUserId UUID userId, @PathVariable UUID id) {
        challengeService.delete(userId, id);
    }

    @Operation(summary = "모집 마감 + 크루 자동 생성",
               description = "주최자가 모집을 마감합니다. 수락된 참여자로 크루가 자동 생성됩니다.")
    @PostMapping("/{id}/close")
    public ApiResponse<CloseChallengeResponse> close(
            @CurrentUserId UUID userId,
            @PathVariable UUID id) {
        return ApiResponse.ok(challengeService.close(userId, id));
    }

    @Operation(summary = "참여 신청",
               description = "챌린지에 참여 신청합니다. 본인 챌린지 신청 불가, 중복 신청 불가.")
    @PostMapping("/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> apply(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody @Valid ApplyRequest request) {
        participationService.apply(userId, id, request);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "신청자 목록 조회 (주최자만)",
               description = "해당 챌린지의 모든 참여 신청자 목록을 반환합니다.")
    @GetMapping("/{id}/applicants")
    public ApiResponse<List<ApplicantResponse>> getApplicants(
            @CurrentUserId UUID userId,
            @PathVariable UUID id) {
        return ApiResponse.ok(participationService.getApplicants(userId, id));
    }
}
