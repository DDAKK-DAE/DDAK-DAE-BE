package com.hackathon.api.domain.crewmessage.repository;

import com.hackathon.api.domain.crewmessage.entity.CrewMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CrewMessageRepository extends JpaRepository<CrewMessage, UUID> {

    List<CrewMessage> findByCrew_IdOrderByCreatedAtAsc(UUID crewId, Pageable pageable);

    List<CrewMessage> findByCrew_IdAndCreatedAtAfterOrderByCreatedAtAsc(UUID crewId, LocalDateTime after, Pageable pageable);
}
