package com.hackathon.api.domain.reel.repository;

import com.hackathon.api.domain.reel.entity.ReelParticipant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReelParticipantRepository extends JpaRepository<ReelParticipant, UUID> {

    /**
     * 릴스 참여자 목록 조회 — JOIN FETCH로 user를 한 번에 로드해 N+1을 방지한다.
     * 피드/아카이브에서 릴스마다 참여자 프로필을 표시할 때 호출된다.
     */
    @Query("SELECT rp FROM ReelParticipant rp JOIN FETCH rp.user WHERE rp.reel.id = :reelId")
    List<ReelParticipant> findByReel_Id(@Param("reelId") UUID reelId);

    /**
     * 릴스 ID 목록으로 참여자를 한 번에 조회 — N+1 방지용 배치 조회.
     * 피드/이력 API에서 릴스 목록을 먼저 조회한 뒤 참여자를 단일 쿼리로 가져올 때 사용한다.
     */
    @Query("SELECT rp FROM ReelParticipant rp JOIN FETCH rp.user WHERE rp.reel.id IN :reelIds")
    List<ReelParticipant> findByReel_IdIn(@Param("reelIds") List<UUID> reelIds);

    /**
     * 유저 참여 릴스 이력 조회 — reel_participants 기반으로 해당 유저가 등록된 릴스를 최신순으로 반환한다.
     * JOIN FETCH rp.reel로 릴스 정보를 한 번에 로드해 N+1을 방지한다.
     */
    @Query("SELECT rp FROM ReelParticipant rp JOIN FETCH rp.reel WHERE rp.user.id = :userId ORDER BY rp.reel.createdAt DESC")
    List<ReelParticipant> findByUser_Id(@Param("userId") UUID userId, Pageable pageable);
}
