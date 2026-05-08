package com.hackathon.api.domain.reel.repository;

import com.hackathon.api.domain.reel.entity.ReelParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReelParticipantRepository extends JpaRepository<ReelParticipant, UUID> {

    @Query("SELECT rp FROM ReelParticipant rp JOIN FETCH rp.user WHERE rp.reel.id = :reelId")
    List<ReelParticipant> findByReel_Id(@Param("reelId") UUID reelId);
}
