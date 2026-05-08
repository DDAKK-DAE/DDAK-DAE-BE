package com.hackathon.api.domain.reel.repository;

import com.hackathon.api.domain.reel.entity.Reel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReelRepository extends JpaRepository<Reel, UUID> {

    List<Reel> findByCrew_Id(UUID crewId);

    List<Reel> findByCrew_IdAndReelType(UUID crewId, String reelType);
}
