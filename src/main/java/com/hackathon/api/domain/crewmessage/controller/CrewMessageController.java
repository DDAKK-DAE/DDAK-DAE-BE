package com.hackathon.api.domain.crewmessage.controller;

import com.hackathon.api.domain.crewmessage.dto.CrewMessageRequest;
import com.hackathon.api.domain.crewmessage.dto.CrewMessageResponse;
import com.hackathon.api.domain.crewmessage.service.CrewMessageService;
import com.hackathon.api.global.response.ApiResponse;
import com.hackathon.api.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Crew Messages", description = "크루 채팅 (WebSocket STOMP + REST 이력 조회)")
@RestController
@RequiredArgsConstructor
public class CrewMessageController {

    private final CrewMessageService crewMessageService;

    /**
     * [HTTP] 채팅 이력 조회 — 입장 시 기존 메시지를 한 번에 로드.
     * GET /crews/{crewId}/messages
     */
    @Operation(summary = "채팅 이력 조회", description = "크루 입장 시 기존 메시지 목록을 반환. 크루 멤버만 접근 가능.")
    @GetMapping("/crews/{crewId}/messages")
    public ApiResponse<List<CrewMessageResponse>> getHistory(
            @PathVariable UUID crewId,
            @CurrentUserId UUID userId) {
        return ApiResponse.ok(crewMessageService.getHistory(crewId, userId));
    }

    /**
     * [WebSocket STOMP] 메시지 전송.
     * 클라이언트 발신 destination: /app/crews/{crewId}/messages
     * 브로드캐스트 destination:   /topic/crews/{crewId}
     *
     * CONNECT 헤더에 Authorization: Bearer {token} 필요.
     * Principal 은 StompAuthChannelInterceptor 에서 JWT 기반으로 주입된다.
     */
    @MessageMapping("/crews/{crewId}/messages")
    public void sendMessage(
            @DestinationVariable UUID crewId,
            @Payload @Valid CrewMessageRequest request,
            Principal principal) {
        UUID senderId = UUID.fromString(principal.getName());
        crewMessageService.sendAndBroadcast(crewId, request.content(), senderId);
    }
}
