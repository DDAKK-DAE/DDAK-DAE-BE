package com.hackathon.api.domain.participation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(name = "applicant_user_id", nullable = false)
    private UUID applicantUserId;

    @Column(name = "intro_message", length = 200)
    private String introMessage;

    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "pending";
    }

    @Builder
    public Participation(UUID challengeId, UUID applicantUserId, String introMessage) {
        this.challengeId = challengeId;
        this.applicantUserId = applicantUserId;
        this.introMessage = introMessage;
        this.status = "pending";
    }

    public void accept() {
        this.status = "accepted";
    }

    public void reject() {
        this.status = "rejected";
    }

    public boolean isPending() {
        return "pending".equals(this.status);
    }

    public boolean isAccepted() {
        return "accepted".equals(this.status);
    }
}
