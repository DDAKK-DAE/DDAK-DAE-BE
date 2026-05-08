package com.hackathon.api.domain.crew.repository;

import com.hackathon.api.domain.crew.entity.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CrewRepository extends JpaRepository<Crew, UUID> {

    /**
     * 챌린지 ID로 크루 조회.
     * POST /challenges/{id}/close 에서 중복 생성 여부 확인 및 크루 참조에 사용.
     */
    Optional<Crew> findByChallenge_Id(UUID challengeId);
}
