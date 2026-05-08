package com.hackathon.api.domain.crewmessage.service;

import com.hackathon.api.domain.crew.entity.Crew;
import com.hackathon.api.domain.crew.repository.CrewMemberRepository;
import com.hackathon.api.domain.crew.repository.CrewRepository;
import com.hackathon.api.domain.crewmessage.dto.CrewMessageResponse;
import com.hackathon.api.domain.crewmessage.entity.CrewMessage;
import com.hackathon.api.domain.crewmessage.exception.CrewMessageErrorCode;
import com.hackathon.api.domain.crewmessage.repository.CrewMessageRepository;
import com.hackathon.api.domain.user.entity.User;
import com.hackathon.api.domain.user.repository.UserRepository;
import com.hackathon.api.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewMessageService {

    private final CrewMessageRepository crewMessageRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송 — DB 저장 후 해당 크루 토픽으로 브로드캐스트.
     * WebSocket(@MessageMapping)과 REST(POST) 양쪽에서 공통으로 호출한다.
     */
    @Transactional
    public CrewMessageResponse sendAndBroadcast(UUID crewId, String content, UUID senderId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new BusinessException(CrewMessageErrorCode.CREW_NOT_FOUND));

        if (!crewMemberRepository.existsByCrew_IdAndUser_Id(crewId, senderId)) {
            throw new BusinessException(CrewMessageErrorCode.NOT_A_MEMBER);
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(CrewMessageErrorCode.USER_NOT_FOUND));

        CrewMessage message = CrewMessage.builder()
                .crew(crew)
                .sender(sender)
                .content(content)
                .build();
        crewMessageRepository.save(message);

        CrewMessageResponse response = CrewMessageResponse.from(message);
        // DB 커밋 후 브로드캐스트 — 커밋 실패 시 클라이언트에 미발송 보장
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                messagingTemplate.convertAndSend("/topic/crews/" + crewId, response);
            }
        });
        return response;
    }

    /** 채팅 이력 조회 — 입장 시 기존 메시지를 한 번에 불러올 때 사용 (HTTP GET) */
    public List<CrewMessageResponse> getHistory(UUID crewId, UUID currentUserId) {
        if (!crewRepository.existsById(crewId)) {
            throw new BusinessException(CrewMessageErrorCode.CREW_NOT_FOUND);
        }
        if (!crewMemberRepository.existsByCrew_IdAndUser_Id(crewId, currentUserId)) {
            throw new BusinessException(CrewMessageErrorCode.NOT_A_MEMBER);
        }
        return crewMessageRepository.findByCrew_IdOrderByCreatedAtAsc(crewId, PageRequest.ofSize(200))
                .stream().map(CrewMessageResponse::from).toList();
    }
}
