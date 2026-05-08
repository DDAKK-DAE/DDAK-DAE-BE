-- ============================================================
-- V2__domain_schema.sql  —  도메인 테이블 전체
-- ============================================================

-- ── users ────────────────────────────────────────────────
CREATE TABLE users (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    nickname       VARCHAR(50)  NOT NULL,
    profile_image  TEXT,
    bio            VARCHAR(150),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── challenges ───────────────────────────────────────────
CREATE TABLE challenges (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    host_user_id         UUID         NOT NULL REFERENCES users(id),
    title                VARCHAR(100) NOT NULL,
    description          TEXT,
    location_text        VARCHAR(100) NOT NULL,
    category             VARCHAR(30)  NOT NULL,
    max_participants     SMALLINT     NOT NULL CHECK (max_participants BETWEEN 2 AND 6),
    current_participants SMALLINT     NOT NULL DEFAULT 0,
    status               VARCHAR(20)  NOT NULL DEFAULT 'open'
                             CHECK (status IN ('open', 'closed', 'done')),
    deadline_at          TIMESTAMP    NOT NULL,
    audio_url            TEXT,
    hashtags             TEXT[],
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── participations ───────────────────────────────────────
CREATE TABLE participations (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    challenge_id      UUID         NOT NULL REFERENCES challenges(id),
    applicant_user_id UUID         NOT NULL REFERENCES users(id),
    intro_message     VARCHAR(200),
    status            VARCHAR(20)  NOT NULL DEFAULT 'pending'
                          CHECK (status IN ('pending', 'accepted', 'rejected')),
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (challenge_id, applicant_user_id)
);

-- ── crews ────────────────────────────────────────────────
CREATE TABLE crews (
    id           UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    challenge_id UUID      NOT NULL UNIQUE REFERENCES challenges(id),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── crew_members ─────────────────────────────────────────
CREATE TABLE crew_members (
    id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    crew_id    UUID      NOT NULL REFERENCES crews(id),
    user_id    UUID      NOT NULL REFERENCES users(id),
    joined_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (crew_id, user_id)
);

-- ── crew_messages ────────────────────────────────────────
CREATE TABLE crew_messages (
    id             UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    crew_id        UUID      NOT NULL REFERENCES crews(id),
    sender_user_id UUID      NOT NULL REFERENCES users(id),
    content        TEXT      NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── reels ────────────────────────────────────────────────
CREATE TABLE reels (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    challenge_id     UUID        NOT NULL REFERENCES challenges(id),
    crew_id          UUID        REFERENCES crews(id),
    uploader_user_id UUID        NOT NULL REFERENCES users(id),
    video_url        TEXT        NOT NULL,
    reel_type        VARCHAR(20) NOT NULL
                         CHECK (reel_type IN ('recruitment', 'completion')),
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    CHECK (
        (reel_type = 'recruitment' AND crew_id IS NULL) OR
        (reel_type = 'completion'  AND crew_id IS NOT NULL)
    )
);

-- ── reel_participants ────────────────────────────────────
CREATE TABLE reel_participants (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reel_id UUID NOT NULL REFERENCES reels(id),
    user_id UUID NOT NULL REFERENCES users(id),
    UNIQUE (reel_id, user_id)
);

-- ── crew_recommendations ─────────────────────────────────
CREATE TABLE crew_recommendations (
    id              UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    crew_id         UUID      NOT NULL REFERENCES crews(id),
    recommendations JSONB     NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── 인덱스 ───────────────────────────────────────────────
CREATE INDEX idx_challenges_status_created   ON challenges    (status, created_at DESC);
CREATE INDEX idx_challenges_category_status  ON challenges    (category, status);
CREATE INDEX idx_participations_ch_status    ON participations(challenge_id, status);
CREATE INDEX idx_crew_messages_crew_created  ON crew_messages (crew_id, created_at);
CREATE INDEX idx_reels_challenge_type        ON reels         (challenge_id, reel_type);
CREATE INDEX idx_reel_participants_user      ON reel_participants(user_id);
