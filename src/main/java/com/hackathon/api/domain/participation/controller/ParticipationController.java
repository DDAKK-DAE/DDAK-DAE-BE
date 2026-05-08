package com.hackathon.api.domain.participation.controller;

import com.hackathon.api.domain.participation.service.ParticipationService;
import com.hackathon.api.global.response.ApiResponse;
import com.hackathon.api.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Participations", description = "신청 수락 / 거절")
@RestController
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @Operation(summary = "신청 수락 (주최자만)",
               description = "참여 신청을 수락합니다. 수락 시 챌린지의 현재 참여자 수가 증가합니다.")
    @PatchMapping("/{id}/accept")
    public ApiResponse<Void> accept(@CurrentUserId UUID userId, @PathVariable UUID id) {
        participationService.accept(userId, id);
        return ApiResponse.ok(null);
    }

    @Operation(summary = "신청 거절 (주최자만)",
               description = "참여 신청을 거절합니다.")
    @PatchMapping("/{id}/reject")
    public ApiResponse<Void> reject(@CurrentUserId UUID userId, @PathVariable UUID id) {
        participationService.reject(userId, id);
        return ApiResponse.ok(null);
    }
}
