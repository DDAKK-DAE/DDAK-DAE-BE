package com.hackathon.api.global.security;

import com.hackathon.api.domain.crew.repository.CrewMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * STOMP 프레임 단위 보안 인터셉터.
 *
 * CONNECT — JWT 검증 후 세션에 Principal 주입.
 * SUBSCRIBE — /topic/crews/{crewId} 구독 시 크루 멤버십 검증.
 *             비멤버가 구독하면 AccessDeniedException → 클라이언트에 ERROR 프레임 전송.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String CREW_TOPIC_PREFIX = "/topic/crews/";

    private final JwtUtil jwtUtil;
    private final CrewMemberRepository crewMemberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            handleSubscribe(accessor);
        }

        return message;
    }

    /**
     * CONNECT: Authorization 헤더에서 JWT를 파싱해 Principal로 세팅.
     * 토큰이 없거나 파싱 실패 시 Principal 없이 연결 — 이후 SUBSCRIBE/SEND에서 차단된다.
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtUtil.parseUserId(authHeader.substring(7)).ifPresent(userId ->
                    accessor.setUser(
                            new UsernamePasswordAuthenticationToken(userId, null, List.of())));
        }
    }

    /**
     * SUBSCRIBE: /topic/crews/{crewId} 구독 요청 시 크루 멤버 여부를 검증.
     * 비멤버 또는 미인증 사용자는 AccessDeniedException 으로 즉시 거부한다.
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(CREW_TOPIC_PREFIX)) return;

        // /topic/crews/{crewId} 에서 crewId 추출
        String crewIdStr = destination.substring(CREW_TOPIC_PREFIX.length()).split("/")[0];

        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new AccessDeniedException("인증된 사용자만 구독할 수 있습니다.");
        }

        try {
            UUID crewId = UUID.fromString(crewIdStr);
            UUID userId = UUID.fromString(principal.getName());

            if (!crewMemberRepository.existsByCrew_IdAndUser_Id(crewId, userId)) {
                throw new AccessDeniedException("크루 멤버만 구독할 수 있습니다.");
            }
        } catch (IllegalArgumentException e) {
            // crewId가 UUID 형식이 아닌 경우 무시 (다른 topic일 수 있음)
        }
    }
}
