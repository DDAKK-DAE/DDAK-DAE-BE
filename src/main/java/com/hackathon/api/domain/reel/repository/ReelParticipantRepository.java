package com.hackathon.api.domain.reel.repository;

import com.hackathon.api.domain.reel.entity.ReelParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReelParticipantRepository extends JpaRepository<ReelParticipant, UUID> {

    List<ReelParticipant> findByReel_Id(UUID reelId);
}
