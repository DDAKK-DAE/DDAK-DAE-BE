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
import java.util.UUID;

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

        List<ReelResponse> reelResponses = reels.stream()
                .map(reel -> {
                    List<ReelParticipant> participants = reelParticipantRepository.findByReel_Id(reel.getId());
                    return ReelResponse.from(reel, participants);
                })
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
        List<ReelParticipant> myEntries = reelParticipantRepository.findByUser_Id(
                userId, PageRequest.ofSize(50));

        return myEntries.stream()
                .map(rp -> {
                    List<ReelParticipant> participants = reelParticipantRepository.findByReel_Id(rp.getReel().getId());
                    return ReelResponse.from(rp.getReel(), participants);
                })
                .toList();
    }
}
