package com.hackathon.api.domain.crew.entity;

import com.hackathon.api.domain.challenge.entity.Challenge;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 크루 — 챌린지 모집 마감 시 자동 생성되는 그룹.
 * 챌린지 1개당 크루 1개만 존재하므로 challenge_id 에 UNIQUE 제약을 건다.
 */
@Entity
@Table(name = "crews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // UNIQUE 제약으로 챌린지당 크루 1개임을 DB 레벨에서 보장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false, unique = true)
    private Challenge challenge;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Crew(Challenge challenge) {
        this.challenge = challenge;
    }
}
