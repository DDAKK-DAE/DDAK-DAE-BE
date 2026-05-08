package com.hackathon.api.domain.crew.service;

import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.challenge.repository.ChallengeRepository;
import com.hackathon.api.domain.crew.dto.CrewArchiveReelResponse;
import com.hackathon.api.domain.crew.dto.CrewDetailResponse;
import com.hackathon.api.domain.crew.dto.CrewResponse;
import com.hackathon.api.domain.crew.dto.MyCrewResponse;
import com.hackathon.api.domain.crew.entity.Crew;
import com.hackathon.api.domain.crew.entity.CrewMember;
import com.hackathon.api.domain.crew.exception.CrewErrorCode;
import com.hackathon.api.domain.crew.repository.CrewMemberRepository;
import com.hackathon.api.domain.crew.repository.CrewRepository;
import com.hackathon.api.domain.reel.entity.Reel;
import com.hackathon.api.domain.reel.entity.ReelParticipant;
import com.hackathon.api.domain.reel.repository.ReelParticipantRepository;
import com.hackathon.api.domain.reel.repository.ReelRepository;
import com.hackathon.api.domain.user.entity.User;
import com.hackathon.api.domain.user.repository.UserRepository;
import com.hackathon.api.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ReelRepository reelRepository;
    private final ReelParticipantRepository reelParticipantRepository;

    /**
     * 크루 생성 — POST /challenges/{id}/close 에서 A 팀(ChallengeService)이 호출한다.
     * 크루 INSERT + 멤버 전원 INSERT 를 하나의 트랜잭션으로 묶어 정합성을 보장한다.
     * 주최자(hostUserId)는 acceptedUserIds 에 포함되지 않더라도 반드시 첫 번째 멤버로 추가된다.
     */
    @Transactional
    public CrewResponse createCrew(UUID challengeId, UUID hostUserId, List<UUID> acceptedUserIds) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(CrewErrorCode.CHALLENGE_NOT_FOUND));

        if (crewRepository.findByChallengeId(challengeId).isPresent()) {
            throw new BusinessException(CrewErrorCode.CREW_ALREADY_EXISTS);
        }

        Crew crew = Crew.builder().challenge(challenge).build();
        try {
            crewRepository.saveAndFlush(crew);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(CrewErrorCode.CREW_ALREADY_EXISTS);
        }

        List<UUID> allMemberIds = new ArrayList<>();

        // 주최자를 첫 멤버로 등록
        User host = userRepository.findById(hostUserId)
                .orElseThrow(() -> new BusinessException(CrewErrorCode.USER_NOT_FOUND));
        crewMemberRepository.save(CrewMember.builder().crew(crew).user(host).build());
        allMemberIds.add(hostUserId);

        // 수락된 참여자 등록 (중복 제거 후 주최자 스킵)
        for (UUID userId : new LinkedHashSet<>(acceptedUserIds)) {
            if (userId.equals(hostUserId)) continue;
            User member = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(CrewErrorCode.USER_NOT_FOUND));
            crewMemberRepository.save(CrewMember.builder().crew(crew).user(member).build());
            allMemberIds.add(userId);
        }

        return new CrewResponse(crew.getId(), allMemberIds);
    }

    /**
     * 크루 상세 조회 — GET /crews/{id}
     * 크루 멤버만 접근 가능하므로 existsByCrew_IdAndUser_Id 로 멤버십을 먼저 확인한다.
     */
    public CrewDetailResponse getCrewDetail(UUID crewId, UUID currentUserId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new BusinessException(CrewErrorCode.CREW_NOT_FOUND));

        checkMembership(crewId, currentUserId);

        List<CrewMember> members = crewMemberRepository.findByCrew_Id(crewId);
        return CrewDetailResponse.from(crew, members);
    }

    /**
     * 크루 릴스 아카이브 조회 — GET /crews/{id}/archive
     * 해당 크루의 완료 릴스(reel_type='completion')와 각 릴스 참여자를 반환한다.
     */
    public List<CrewArchiveReelResponse> getCrewArchive(UUID crewId, UUID currentUserId) {
        if (!crewRepository.existsById(crewId)) {
            throw new BusinessException(CrewErrorCode.CREW_NOT_FOUND);
        }

        checkMembership(crewId, currentUserId);

        List<Reel> reels = reelRepository.findByCrew_IdAndReelType(crewId, "completion",
                PageRequest.of(0, 50, Sort.by("createdAt").descending()));
        return reels.stream().map(reel -> {
            List<ReelParticipant> participants = reelParticipantRepository.findByReel_Id(reel.getId());
            return CrewArchiveReelResponse.from(reel, participants);
        }).toList();
    }

    /**
     * 내 크루 목록 조회 — GET /me/crews
     * crew_members 테이블에서 해당 유저가 속한 크루를 조회하고 각 크루의 멤버 수를 집계한다.
     */
    public List<MyCrewResponse> getMyCrews(UUID userId) {
        List<CrewMember> memberships = crewMemberRepository.findByUser_Id(userId);
        return memberships.stream()
                .map(m -> {
                    long memberCount = crewMemberRepository.countByCrew_Id(m.getCrew().getId());
                    return MyCrewResponse.from(m.getCrew(), memberCount);
                })
                .toList();
    }

    /** 멤버십 검증 헬퍼 — 비멤버면 403 을 발생시킨다 */
    private void checkMembership(UUID crewId, UUID userId) {
        if (!crewMemberRepository.existsByCrew_IdAndUser_Id(crewId, userId)) {
            throw new BusinessException(CrewErrorCode.NOT_A_MEMBER);
        }
    }
}
