package com.hackathon.api.domain.reel.service;

import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.challenge.repository.ChallengeRepository;
import com.hackathon.api.domain.crew.entity.Crew;
import com.hackathon.api.domain.crew.entity.CrewMember;
import com.hackathon.api.domain.crew.repository.CrewMemberRepository;
import com.hackathon.api.domain.crew.repository.CrewRepository;
import com.hackathon.api.domain.reel.dto.ChallengeReelFeedResponse;
import com.hackathon.api.domain.reel.dto.ReelResponse;
import com.hackathon.api.domain.reel.dto.ReelUploadRequest;
import com.hackathon.api.domain.reel.entity.Reel;
import com.hackathon.api.domain.reel.entity.ReelParticipant;
import com.hackathon.api.domain.reel.exception.ReelErrorCode;
import com.hackathon.api.domain.reel.repository.ReelParticipantRepository;
import com.hackathon.api.domain.reel.repository.ReelRepository;
import com.hackathon.api.domain.user.entity.User;
import com.hackathon.api.domain.user.repository.UserRepository;
import com.hackathon.api.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReelService {

    private final ReelRepository reelRepository;
    private final ReelParticipantRepository reelParticipantRepository;
    private final ChallengeRepository challengeRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final UserRepository userRepository;

    /**
     * 릴스 업로드.
     * crewId가 없으면 모집용(recruitment), 있으면 완료용(completion).
     * 완료용은 업로더가 크루 멤버인지 검증 후 크루원 전원을 reel_participants에 자동 등록한다.
     * 모집용은 업로더만 participant로 등록한다.
     */
    @Transactional
    public ReelResponse upload(UUID uploaderId, ReelUploadRequest req) {
        Challenge challenge = challengeRepository.findById(req.challengeId())
                .orElseThrow(() -> new BusinessException(ReelErrorCode.CHALLENGE_NOT_FOUND));

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new BusinessException(ReelErrorCode.USER_NOT_FOUND));

        Crew crew = null;
        String reelType;

        if (req.crewId() != null) {
            crew = crewRepository.findById(req.crewId())
                    .orElseThrow(() -> new BusinessException(ReelErrorCode.CREW_NOT_FOUND));
            // 크루가 요청한 챌린지 소속인지 검증 — 타 챌린지 크루로 완료 릴스 등록 방지
            if (!crew.getChallenge().getId().equals(req.challengeId())) {
                throw new BusinessException(ReelErrorCode.CREW_CHALLENGE_MISMATCH);
            }
            if (!crewMemberRepository.existsByCrew_IdAndUser_Id(req.crewId(), uploaderId)) {
                throw new BusinessException(ReelErrorCode.NOT_A_CREW_MEMBER);
            }
            reelType = "completion";
        } else {
            reelType = "recruitment";
        }

        Reel reel = Reel.builder()
                .challenge(challenge)
                .crew(crew)
                .uploader(uploader)
                .videoUrl(req.videoUrl())
                .reelType(reelType)
                .build();
        reelRepository.save(reel);

        List<ReelParticipant> participants;
        if ("completion".equals(reelType)) {
            // 크루원 전원 자동 등록
            List<CrewMember> crewMembers = crewMemberRepository.findByCrew_Id(req.crewId());
            participants = crewMembers.stream()
                    .map(m -> ReelParticipant.builder().reel(reel).user(m.getUser()).build())
                    .toList();
        } else {
            // 모집용은 업로더만 등록
            participants = List.of(ReelParticipant.builder().reel(reel).user(uploader).build());
        }
        reelParticipantRepository.saveAll(participants);

        return ReelResponse.from(reel, participants);
    }

    /**
     * 챌린지별 릴스 피드 조회.
     * type 파라미터로 recruitment / completion 필터링, 없으면 전체 반환.
     * 챌린지의 audio_url을 함께 내려 프론트에서 음원 통일 재생에 사용한다.
     */
    public ChallengeReelFeedResponse getChallengeReels(UUID challengeId, String type) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(ReelErrorCode.CHALLENGE_NOT_FOUND));

        PageRequest pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());

        List<Reel> reels = (type != null && !type.isBlank())
                ? reelRepository.findByChallenge_IdAndReelTypeOrderByCreatedAtDesc(challengeId, type, pageable)
                : reelRepository.findByChallenge_IdOrderByCreatedAtDesc(challengeId, pageable);

        // 릴스 ID 목록으로 참여자를 한 번에 조회해 N+1 방지
        List<UUID> reelIds = reels.stream().map(Reel::getId).toList();
        Map<UUID, List<ReelParticipant>> participantsByReelId =
                reelParticipantRepository.findByReel_IdIn(reelIds).stream()
                        .collect(Collectors.groupingBy(rp -> rp.getReel().getId()));

        List<ReelResponse> reelResponses = reels.stream()
                .map(reel -> ReelResponse.from(
                        reel,
                        participantsByReelId.getOrDefault(reel.getId(), List.of())))
                .toList();

        // 챌린지가 마감되어 크루가 생성된 경우 crewId 포함 — 프론트에서 크루 공간으로 이동에 사용
        UUID crewId = crewRepository.findByChallengeId(challengeId)
                .map(Crew::getId)
                .orElse(null);

        return new ChallengeReelFeedResponse(challenge.getAudioUrl(), crewId, reelResponses);
    }

    /**
     * 유저 릴스 이력 조회 — reel_participants 기반으로 해당 유저가 참여한 릴스를 반환한다.
     * 마이페이지에서 본인 이력 조회 및 타 유저 프로필 조회 양쪽에서 사용한다.
     */
    public List<ReelResponse> getUserReels(UUID userId) {
        // 1단계: JOIN FETCH 없이 ID만 페이지네이션 조회 — HHH000104 메모리 페이지네이션 경고 방지
        List<UUID> ids = reelParticipantRepository.findIdsByUserId(userId, PageRequest.ofSize(50));
        if (ids.isEmpty()) return List.of();

        // 2단계: ID 목록으로 reel JOIN FETCH 조회
        List<ReelParticipant> myEntries = reelParticipantRepository.findByIdInWithReel(ids);

        // 릴스 ID 목록으로 참여자를 한 번에 조회해 N+1 방지
        List<UUID> reelIds = myEntries.stream().map(rp -> rp.getReel().getId()).toList();
        Map<UUID, List<ReelParticipant>> participantsByReelId =
                reelParticipantRepository.findByReel_IdIn(reelIds).stream()
                        .collect(Collectors.groupingBy(rp -> rp.getReel().getId()));

        return myEntries.stream()
                .map(rp -> ReelResponse.from(
                        rp.getReel(),
                        participantsByReelId.getOrDefault(rp.getReel().getId(), List.of())))
                .toList();
    }
}
