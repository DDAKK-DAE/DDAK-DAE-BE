package com.hackathon.api.domain.challenge.repository;

import com.hackathon.api.domain.challenge.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {

    @Query("""
        SELECT c FROM Challenge c
        WHERE c.status = 'open'
          AND (:category IS NULL OR c.category = :category)
          AND (:location IS NULL OR c.locationText LIKE %:location%)
        """)
    Page<Challenge> findFeed(@Param("category") String category,
                             @Param("location") String location,
                             Pageable pageable);
}
