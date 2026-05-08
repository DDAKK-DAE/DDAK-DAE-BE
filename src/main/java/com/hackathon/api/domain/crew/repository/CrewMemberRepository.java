package com.hackathon.api.domain.crew.repository;

import com.hackathon.api.domain.crew.entity.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CrewMemberRepository extends JpaRepository<CrewMember, UUID> {

    List<CrewMember> findByCrew_Id(UUID crewId);

    List<CrewMember> findByUser_Id(UUID userId);

    boolean existsByCrew_IdAndUser_Id(UUID crewId, UUID userId);

    long countByCrew_Id(UUID crewId);
}
