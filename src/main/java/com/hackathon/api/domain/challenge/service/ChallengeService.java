package com.hackathon.api.domain.challenge.service;

import com.hackathon.api.domain.challenge.dto.*;
import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.challenge.exception.ChallengeErrorCode;
import com.hackathon.api.domain.challenge.repository.ChallengeRepository;
import com.hackathon.api.domain.participation.entity.Participation;
import com.hackathon.api.domain.participation.repository.ParticipationRepository;
import com.hackathon.api.domain.user.entity.User;
import com.hackathon.api.domain.user.repository.UserRepository;
import com.hackathon.api.global.exception.BusinessException;
import com.hackathon.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public Page<ChallengeListItemResponse> getFeed(UUID userId, String category, String location, Pageable pageable) {
        Page<Challenge> page = challengeRepository.findFeed(category, location, pageable);
        List<Challenge> challenges = page.getContent();

        // 유저의 accepted 참여 카테고리 빈도 스코어 계산
        Map<String, Long> categoryScore = new HashMap<>();
        if (userId != null) {
            participationRepository.findByApplicantUserIdAndStatus(userId, "accepted")
                    .stream()
                    .map(p -> {
                        return challengeRepository.findById(p.getChallengeId())
                                .map(Challenge::getCategory)
                                .orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .forEach(cat -> categoryScore.merge(cat, 1L, Long::sum));
        }

        // host 정보 한 번에 조회 (N+1 방지)
        Set<UUID> hostIds = challenges.stream().map(Challenge::getHostUserId).collect(Collectors.toSet());
        Map<UUID, User> hostMap = userRepository.findAllById(hostIds)
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        // 개인화 정렬: 카테고리 스코어 높은 순 → createdAt 최신 순
        List<ChallengeListItemResponse> sorted = challenges.stream()
                .sorted(Comparator
                        .comparingLong((Challenge c) -> categoryScore.getOrDefault(c.getCategory(), 0L))
                        .reversed()
                        .thenComparing(Comparator.comparing(Challenge::getCreatedAt).reversed()))
                .map(c -> ChallengeListItemResponse.of(c, hostMap.get(c.getHostUserId())))
                .toList();

        return new PageImpl<>(sorted, pageable, page.getTotalElements());
    }

    public ChallengeDetailResponse getDetail(UUID id) {
        Challenge challenge = findById(id);
        User host = userRepository.findById(challenge.getHostUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return ChallengeDetailResponse.of(challenge, host);
    }

    @Transactional
    public ChallengeDetailResponse create(UUID userId, CreateChallengeRequest req) {
        Challenge challenge = Challenge.builder()
                .hostUserId(userId)
                .title(req.title())
                .description(req.description())
                .locationText(req.locationText())
                .category(req.category())
                .maxParticipants(req.maxParticipants().shortValue())
                .deadlineAt(req.deadlineAt())
                .audioUrl(req.audioUrl())
                .hashtags(req.hashtags())
                .build();

        challengeRepository.save(challenge);

        User host = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return ChallengeDetailResponse.of(challenge, host);
    }

    @Transactional
    public ChallengeDetailResponse update(UUID userId, UUID challengeId, UpdateChallengeRequest req) {
        Challenge challenge = findById(challengeId);
        validateHost(challenge, userId);

        challenge.update(
                req.title(), req.description(), req.locationText(), req.category(),
                req.maxParticipants() != null ? req.maxParticipants().shortValue() : null,
                req.deadlineAt(), req.audioUrl(), req.hashtags()
        );

        User host = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return ChallengeDetailResponse.of(challenge, host);
    }

    @Transactional
    public void delete(UUID userId, UUID challengeId) {
        Challenge challenge = findById(challengeId);
        validateHost(challenge, userId);
        if (!challenge.isOpen()) {
            throw new BusinessException(ChallengeErrorCode.CANNOT_DELETE_CLOSED);
        }
        challengeRepository.delete(challenge);
    }

    @Transactional
    public CloseChallengeResponse close(UUID userId, UUID challengeId) {
        Challenge challenge = findById(challengeId);
        validateHost(challenge, userId);
        if (!challenge.isOpen()) {
            throw new BusinessException(ChallengeErrorCode.CHALLENGE_NOT_OPEN);
        }

        List<UUID> acceptedUserIds = participationRepository
                .findByChallengeIdAndStatus(challengeId, "accepted")
                .stream().map(Participation::getApplicantUserId).toList();

        challenge.close();

        // TODO: B 파트 CrewService.createCrew(challengeId, userId, acceptedUserIds) 연동 후 crewId 반환
        return new CloseChallengeResponse(null, acceptedUserIds);
    }

    private Challenge findById(UUID id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));
    }

    private void validateHost(Challenge challenge, UUID userId) {
        if (!challenge.isHost(userId)) {
            throw new BusinessException(ChallengeErrorCode.NOT_CHALLENGE_HOST);
        }
    }
}
