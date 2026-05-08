package com.hackathon.api.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(length = 150)
    private String bio;

    @Column(length = 50)
    private String name;

    private java.time.LocalDate birthday;

    private Short age;

    @Column(length = 50)
    private String job;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public User(String email, String passwordHash, String nickname,
                String profileImage, String bio, String name,
                java.time.LocalDate birthday, Short age, String job) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.bio = bio;
        this.name = name;
        this.birthday = birthday;
        this.age = age;
        this.job = job;
    }

    public void updateProfile(String nickname, String bio, String profileImage,
                              String name, java.time.LocalDate birthday, Short age, String job) {
        if (nickname != null) this.nickname = nickname;
        if (bio != null) this.bio = bio;
        if (profileImage != null) this.profileImage = profileImage;
        if (name != null) this.name = name;
        if (birthday != null) this.birthday = birthday;
        if (age != null) this.age = age;
        if (job != null) this.job = job;
    }
}
