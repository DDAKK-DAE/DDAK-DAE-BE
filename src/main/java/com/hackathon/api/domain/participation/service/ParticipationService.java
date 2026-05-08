package com.hackathon.api.domain.participation.service;

import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.challenge.exception.ChallengeErrorCode;
import com.hackathon.api.domain.challenge.repository.ChallengeRepository;
import com.hackathon.api.domain.participation.dto.ApplicantResponse;
import com.hackathon.api.domain.participation.dto.ApplyRequest;
import com.hackathon.api.domain.participation.entity.Participation;
import com.hackathon.api.domain.participation.exception.ParticipationErrorCode;
import com.hackathon.api.domain.participation.repository.ParticipationRepository;
import com.hackathon.api.domain.user.entity.User;
import com.hackathon.api.domain.user.repository.UserRepository;
import com.hackathon.api.global.exception.BusinessException;
import com.hackathon.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void apply(UUID userId, UUID challengeId, ApplyRequest req) {
        Challenge challenge = findChallenge(challengeId);

        if (challenge.isHost(userId)) {
            throw new BusinessException(ParticipationErrorCode.CANNOT_APPLY_OWN_CHALLENGE);
        }
        if (!challenge.isOpen() || !challenge.isBeforeDeadline()) {
            throw new BusinessException(ChallengeErrorCode.CHALLENGE_NOT_OPEN);
        }
        if (participationRepository.existsByChallengeIdAndApplicantUserId(challengeId, userId)) {
            throw new BusinessException(ParticipationErrorCode.ALREADY_APPLIED);
        }

        participationRepository.save(Participation.builder()
                .challengeId(challengeId)
                .applicantUserId(userId)
                .introMessage(req.introMessage())
                .build());
    }

    public List<ApplicantResponse> getApplicants(UUID hostUserId, UUID challengeId) {
        Challenge challenge = findChallenge(challengeId);
        if (!challenge.isHost(hostUserId)) {
            throw new BusinessException(ChallengeErrorCode.NOT_CHALLENGE_HOST);
        }

        List<Participation> participations = participationRepository.findByChallengeId(challengeId);
        Set<UUID> userIds = participations.stream()
                .map(Participation::getApplicantUserId).collect(Collectors.toSet());
        Map<UUID, User> userMap = userRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        return participations.stream()
                .map(p -> ApplicantResponse.of(p, userMap.get(p.getApplicantUserId())))
                .toList();
    }

    @Transactional
    public void accept(UUID hostUserId, UUID participationId) {
        Participation participation = findParticipation(participationId);
        Challenge challenge = findChallenge(participation.getChallengeId());

        if (!challenge.isHost(hostUserId)) {
            throw new BusinessException(ChallengeErrorCode.NOT_CHALLENGE_HOST);
        }
        if (!participation.isPending()) {
            throw new BusinessException(ParticipationErrorCode.NOT_PENDING_PARTICIPATION);
        }
        if (challenge.getCurrentParticipants() >= challenge.getMaxParticipants()) {
            throw new BusinessException(ParticipationErrorCode.CHALLENGE_FULL);
        }

        participation.accept();
        challenge.incrementParticipants();
    }

    @Transactional
    public void reject(UUID hostUserId, UUID participationId) {
        Participation participation = findParticipation(participationId);
        Challenge challenge = findChallenge(participation.getChallengeId());

        if (!challenge.isHost(hostUserId)) {
            throw new BusinessException(ChallengeErrorCode.NOT_CHALLENGE_HOST);
        }
        if (!participation.isPending()) {
            throw new BusinessException(ParticipationErrorCode.NOT_PENDING_PARTICIPATION);
        }

        participation.reject();
    }

    private Challenge findChallenge(UUID challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(ChallengeErrorCode.CHALLENGE_NOT_FOUND));
    }

    private Participation findParticipation(UUID participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new BusinessException(ParticipationErrorCode.PARTICIPATION_NOT_FOUND));
    }
}
