package com.hackathon.api.domain.crew.repository;

import com.hackathon.api.domain.crew.entity.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CrewRepository extends JpaRepository<Crew, UUID> {

    Optional<Crew> findByChallenge_Id(UUID challengeId);
}
