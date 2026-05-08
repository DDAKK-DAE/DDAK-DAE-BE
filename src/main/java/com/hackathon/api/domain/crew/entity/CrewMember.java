package com.hackathon.api.domain.crew.entity;

import com.hackathon.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 크루 멤버 — 크루에 속한 사용자를 나타내는 조인 테이블 엔티티.
 * 크루 생성 시 주최자와 수락된 참여자 전원이 일괄 삽입된다.
 * (crew_id, user_id) UNIQUE 제약으로 중복 가입을 방지한다.
 */
@Entity
@Table(name = "crew_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"crew_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }

    @Builder
    public CrewMember(Crew crew, User user) {
        this.crew = crew;
        this.user = user;
    }
}
