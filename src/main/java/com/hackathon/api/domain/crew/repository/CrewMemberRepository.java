package com.hackathon.api.domain.crew.repository;

import com.hackathon.api.domain.crew.entity.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CrewMemberRepository extends JpaRepository<CrewMember, UUID> {

    /** 크루 상세 조회 및 아카이브에서 멤버 목록을 가져올 때 사용 */
    List<CrewMember> findByCrew_Id(UUID crewId);

    /** 내 크루 목록(GET /me/crews) 조회 시 해당 유저가 속한 모든 크루 멤버십을 가져올 때 사용 */
    List<CrewMember> findByUser_Id(UUID userId);

    /**
     * 크루 멤버 여부 확인 — 멤버 전용 엔드포인트 접근 제어에 사용.
     * 별도 엔티티 조회 없이 존재 여부만 확인하므로 쿼리 비용이 낮다.
     */
    boolean existsByCrew_IdAndUser_Id(UUID crewId, UUID userId);

    /** 내 크루 목록 응답에 포함할 멤버 수를 효율적으로 집계 */
    long countByCrew_Id(UUID crewId);
}
