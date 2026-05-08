package com.hackathon.api.domain.participation.repository;

import com.hackathon.api.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipationRepository extends JpaRepository<Participation, UUID> {

    boolean existsByChallengeIdAndApplicantUserId(UUID challengeId, UUID applicantUserId);

    List<Participation> findByChallengeId(UUID challengeId);

    List<Participation> findByApplicantUserIdAndStatus(UUID applicantUserId, String status);

    List<Participation> findByChallengeIdAndStatus(UUID challengeId, String status);
}
