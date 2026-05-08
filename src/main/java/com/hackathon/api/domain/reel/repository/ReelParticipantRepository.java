package com.hackathon.api.domain.reel.repository;

import com.hackathon.api.domain.reel.entity.ReelParticipant;
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
}
