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
     * CONNECT: JWT 없거나 파싱 실패 시 즉시 거부.
     * principal 없이 세션이 열리면 이후 SEND 에서 NPE 가 발생하므로 여기서 차단한다.
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authorization 헤더가 없습니다.");
        }

        UUID userId = jwtUtil.parseUserId(authHeader.substring(7))
                .orElseThrow(() -> new AccessDeniedException("유효하지 않은 토큰입니다."));

        accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
    }

    /**
     * SUBSCRIBE: /topic/crews/{crewId} 구독 시 크루 멤버 여부 검증.
     * prefix 일치 후 crewId 가 UUID 형식이 아니면 즉시 거부한다.
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(CREW_TOPIC_PREFIX)) return;

        String pathAfterPrefix = destination.substring(CREW_TOPIC_PREFIX.length());
        if (pathAfterPrefix.isEmpty()) {
            throw new AccessDeniedException("crewId가 누락되었습니다.");
        }
        String crewIdStr = pathAfterPrefix.split("/")[0];
        if (crewIdStr.isEmpty()) {
            throw new AccessDeniedException("crewId가 누락되었습니다.");
        }

        // /topic/crews/ 로 시작했는데 UUID가 아니면 거부
        UUID crewId;
        try {
            crewId = UUID.fromString(crewIdStr);
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("잘못된 crewId 형식입니다: " + crewIdStr);
        }

        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new AccessDeniedException("인증된 사용자만 구독할 수 있습니다.");
        }

        UUID userId = UUID.fromString(principal.getName());
        if (!crewMemberRepository.existsByCrew_IdAndUser_Id(crewId, userId)) {
            throw new AccessDeniedException("크루 멤버만 구독할 수 있습니다.");
        }
    }
}
