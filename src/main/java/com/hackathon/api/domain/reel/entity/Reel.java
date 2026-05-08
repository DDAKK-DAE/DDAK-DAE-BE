package com.hackathon.api.domain.reel.entity;

import com.hackathon.api.domain.challenge.entity.Challenge;
import com.hackathon.api.domain.crew.entity.Crew;
import com.hackathon.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_user_id", nullable = false)
    private User uploader;

    @Column(name = "video_url", nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "reel_type", nullable = false, length = 20)
    private String reelType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Reel(Challenge challenge, Crew crew, User uploader, String videoUrl, String reelType) {
        this.challenge = challenge;
        this.crew = crew;
        this.uploader = uploader;
        this.videoUrl = videoUrl;
        this.reelType = reelType;
    }
}
