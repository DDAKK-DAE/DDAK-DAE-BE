package com.hackathon.api.domain.crew.repository;

import com.hackathon.api.domain.crew.entity.Crew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CrewRepository extends JpaRepository<Crew, UUID> {

    /**
     * 챌린지 ID로 크루 조회.
     * 메서드명 파싱 모호성을 피하기 위해 JPQL을 명시적으로 지정한다.
     * POST /challenges/{id}/close 에서 중복 생성 여부 확인 및 크루 참조에 사용.
     */
    @Query("SELECT c FROM Crew c WHERE c.challenge.id = :challengeId")
    Optional<Crew> findByChallengeId(@Param("challengeId") UUID challengeId);
}
