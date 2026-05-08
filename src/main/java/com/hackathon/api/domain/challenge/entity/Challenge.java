package com.hackathon.api.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "host_user_id", nullable = false)
    private UUID hostUserId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "location_text", nullable = false, length = 100)
    private String locationText;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(name = "max_participants", nullable = false)
    private Short maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Short currentParticipants = 1;

    @Column(nullable = false, length = 20)
    private String status = "open";

    @Column(name = "deadline_at", nullable = false)
    private LocalDateTime deadlineAt;

    @Column(name = "audio_url")
    private String audioUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> hashtags;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currentParticipants == null) this.currentParticipants = 1;
        if (this.status == null) this.status = "open";
    }

    @Builder
    public Challenge(UUID hostUserId, String title, String description, String locationText,
                     String category, Short maxParticipants, LocalDateTime deadlineAt,
                     String audioUrl, List<String> hashtags) {
        this.hostUserId = hostUserId;
        this.title = title;
        this.description = description;
        this.locationText = locationText;
        this.category = category;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 1;
        this.status = "open";
        this.deadlineAt = deadlineAt;
        this.audioUrl = audioUrl;
        this.hashtags = hashtags;
    }

    public void update(String title, String description, String locationText, String category,
                       Short maxParticipants, LocalDateTime deadlineAt, String audioUrl, List<String> hashtags) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (locationText != null) this.locationText = locationText;
        if (category != null) this.category = category;
        if (maxParticipants != null) this.maxParticipants = maxParticipants;
        if (deadlineAt != null) this.deadlineAt = deadlineAt;
        if (audioUrl != null) this.audioUrl = audioUrl;
        if (hashtags != null) this.hashtags = hashtags;
    }

    public void close() {
        this.status = "closed";
    }

    public void syncCurrentParticipants(int memberCount) {
        if (memberCount < 0 || memberCount > Short.MAX_VALUE) {
            throw new IllegalArgumentException("memberCount out of range: " + memberCount);
        }
        this.currentParticipants = (short) memberCount;
    }

    public void incrementParticipants() {
        this.currentParticipants++;
    }

    public boolean isHost(UUID userId) {
        return this.hostUserId.equals(userId);
    }

    public boolean isOpen() {
        return "open".equals(this.status);
    }

    public boolean isBeforeDeadline() {
        return LocalDateTime.now().isBefore(this.deadlineAt);
    }
}
