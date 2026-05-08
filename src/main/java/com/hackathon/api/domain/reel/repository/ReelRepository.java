package com.hackathon.api.domain.reel.repository;

import com.hackathon.api.domain.reel.entity.Reel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReelRepository extends JpaRepository<Reel, UUID> {

    /** 크루 아카이브 전체 조회 — CrewService.getCrewArchive()에서 crew_id 기준으로 전체 릴스 조회 시 사용 */
    List<Reel> findByCrew_Id(UUID crewId, Pageable pageable);

    /** 크루 아카이브 completion 필터 — "completion" 타입만 반환해 모집용 릴스가 섞이지 않도록 한다 */
    List<Reel> findByCrew_IdAndReelType(UUID crewId, String reelType, Pageable pageable);

    /** 챌린지 피드 전체 조회 — type 파라미터 없을 때 해당 챌린지의 모든 릴스를 최신순으로 반환 */
    List<Reel> findByChallenge_IdOrderByCreatedAtDesc(UUID challengeId, Pageable pageable);

    /** 챌린지 피드 타입 필터 — "recruitment" 또는 "completion"으로 필터링해 음원 통일 몰아보기에 사용 */
    List<Reel> findByChallenge_IdAndReelTypeOrderByCreatedAtDesc(UUID challengeId, String reelType, Pageable pageable);
}
