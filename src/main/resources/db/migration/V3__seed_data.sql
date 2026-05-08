-- ============================================================
-- V3__seed_data.sql  —  데모·개발 시드 데이터
-- 비밀번호: 모두 "password" (BCrypt, pgcrypto crypt 사용)
-- ============================================================

-- ── 사용자 5명 ───────────────────────────────────────────
INSERT INTO users (email, password_hash, nickname, bio) VALUES
  ('alice@example.com',   crypt('password', gen_salt('bf', 10)), '알리스',   '댄스 챌린지 마스터 🕺'),
  ('bob@example.com',     crypt('password', gen_salt('bf', 10)), '밥',       '스포츠 덕후 🏃'),
  ('charlie@example.com', crypt('password', gen_salt('bf', 10)), '찰리',     '먹방 유튜버 🍜'),
  ('diana@example.com',   crypt('password', gen_salt('bf', 10)), '다이애나', '일상 브이로거 📸'),
  ('evan@example.com',    crypt('password', gen_salt('bf', 10)), '에반',     '챌린지 기획자 🎬');

-- ── 챌린지 20개 (트렌딩/카테고리 분포) ──────────────────
-- 댄스 8 / 일상 5 / 스포츠 3 / 푸드 3 / 기타 1
-- current_participants 분포: 5,5,4,4,4,3,3,3,3,2,2,2,2,1,1,1,0,0,0,0

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '장원영 챌린지 같이 찍어요!',
       '강남역 스타벅스 앞에서 장원영 "Magnetic" 챌린지 같이 찍을 분 구해요. 초보 환영!',
       '강남역',
       '댄스', 5, 5, 'open',
       NOW() + INTERVAL '5 days',
       'https://cdn.example.com/audio/magnetic.mp3',
       ARRAY['#장원영', '#Magnetic', '#댄스챌린지', '#강남']
FROM users u WHERE u.email = 'alice@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       'APT 챌린지 모집 중',
       '홍대 걷고싶은거리에서 로제 APT 챌린지 찍어요~ 커플·친구 모두 환영',
       '홍대입구역',
       '댄스', 4, 5, 'open',
       NOW() + INTERVAL '3 days',
       'https://cdn.example.com/audio/apt.mp3',
       ARRAY['#APT', '#로제', '#홍대', '#댄스']
FROM users u WHERE u.email = 'bob@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '정국 Seven 챌린지 크루 구함',
       '이태원 루프탑에서 Seven 챌린지 촬영! 야경 배경으로 멋진 영상 만들어요',
       '이태원',
       '댄스', 6, 4, 'open',
       NOW() + INTERVAL '7 days',
       'https://cdn.example.com/audio/seven.mp3',
       ARRAY['#정국', '#Seven', '#이태원', '#야경']
FROM users u WHERE u.email = 'charlie@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '뉴진스 Hype Boy 같이 해요',
       '성수동 카페거리 배경으로 Hype Boy 안무 챌린지! 4인 이상 모이면 바로 촬영 고고',
       '성수동',
       '댄스', 4, 4, 'open',
       NOW() + INTERVAL '4 days',
       'https://cdn.example.com/audio/hypeboy.mp3',
       ARRAY['#뉴진스', '#HypeBoy', '#성수동', '#댄스']
FROM users u WHERE u.email = 'diana@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '아이브 Eleven 챌린지',
       '잠실 한강공원에서 Eleven 챌린지. 인원 다 찼으면 대기도 받아요!',
       '잠실 한강공원',
       '댄스', 5, 4, 'open',
       NOW() + INTERVAL '6 days',
       'https://cdn.example.com/audio/eleven.mp3',
       ARRAY['#아이브', '#Eleven', '#한강', '#댄스챌린지']
FROM users u WHERE u.email = 'evan@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       'STAYC ASAP 챌린지',
       '여의도 벚꽃길에서 ASAP 포인트 안무 같이 찍어요',
       '여의도',
       '댄스', 4, 3, 'open',
       NOW() + INTERVAL '8 days',
       'https://cdn.example.com/audio/asap.mp3',
       ARRAY['#STAYC', '#ASAP', '#여의도', '#벚꽃']
FROM users u WHERE u.email = 'alice@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '에스파 Supernova 댄스 크루',
       '코엑스 광장에서 Supernova 합동 챌린지. 의상 맞추면 더 좋음 😎',
       '코엑스',
       '댄스', 6, 3, 'open',
       NOW() + INTERVAL '10 days',
       'https://cdn.example.com/audio/supernova.mp3',
       ARRAY['#에스파', '#Supernova', '#코엑스', '#합동챌린지']
FROM users u WHERE u.email = 'bob@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       'LE SSERAFIM ANTIFRAGILE',
       '북촌 한옥마을 배경으로 ANTIFRAGILE 찍어요. 전통 의상 있으면 착용 환영',
       '북촌 한옥마을',
       '댄스', 4, 3, 'open',
       NOW() + INTERVAL '5 days',
       'https://cdn.example.com/audio/antifragile.mp3',
       ARRAY['#LE_SSERAFIM', '#ANTIFRAGILE', '#한옥마을']
FROM users u WHERE u.email = 'charlie@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '서울 한강 일출 촬영 같이 해요',
       '반포한강공원 새벽 일출 감성 브이로그. 4시 30분 모임, 커피 들고 오세요 ☕',
       '반포한강공원',
       '일상', 4, 3, 'open',
       NOW() + INTERVAL '2 days',
       'https://cdn.example.com/audio/morning_vibe.mp3',
       ARRAY['#한강일출', '#새벽감성', '#브이로그', '#일상']
FROM users u WHERE u.email = 'diana@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '마라톤 훈련 크루 모집',
       '올림픽공원 5km 러닝 + 챌린지 영상. 매주 토요일 오전 7시',
       '올림픽공원',
       '스포츠', 6, 2, 'open',
       NOW() + INTERVAL '4 days',
       'https://cdn.example.com/audio/running.mp3',
       ARRAY['#마라톤', '#러닝크루', '#올림픽공원', '#스포츠']
FROM users u WHERE u.email = 'evan@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '광장시장 먹방 챌린지',
       '광장시장에서 마약김밥+빈대떡 먹방 릴스 같이 찍어요. 많이 드실 분만!',
       '광장시장',
       '푸드', 4, 2, 'open',
       NOW() + INTERVAL '3 days',
       'https://cdn.example.com/audio/mukbang.mp3',
       ARRAY['#광장시장', '#마약김밥', '#먹방챌린지', '#푸드']
FROM users u WHERE u.email = 'alice@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '성수동 카페 투어 브이로그',
       '성수동 인스타 핫플 카페 3곳 투어하면서 릴스 영상 같이 만들어요',
       '성수동',
       '일상', 4, 2, 'open',
       NOW() + INTERVAL '6 days',
       'https://cdn.example.com/audio/cafe_vibe.mp3',
       ARRAY['#성수동', '#카페투어', '#브이로그', '#일상']
FROM users u WHERE u.email = 'bob@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '농구 챌린지 (3점슛 성공 릴스)',
       '잠실 야외농구장에서 3점슛 성공 챌린지. 실력 무관 재미로!',
       '잠실 야외농구장',
       '스포츠', 4, 2, 'open',
       NOW() + INTERVAL '5 days',
       'https://cdn.example.com/audio/basketball.mp3',
       ARRAY['#농구챌린지', '#3점슛', '#잠실', '#스포츠']
FROM users u WHERE u.email = 'charlie@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '남산 타워 야경 감성 릴스',
       '남산타워 불빛 배경으로 감성 릴스. 황혼 시간대 촬영 예정',
       '남산타워',
       '일상', 4, 1, 'open',
       NOW() + INTERVAL '7 days',
       'https://cdn.example.com/audio/nightview.mp3',
       ARRAY['#남산타워', '#야경', '#감성릴스', '#서울']
FROM users u WHERE u.email = 'diana@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '홍대 길거리 퍼포먼스 챌린지',
       '홍대 놀이터 앞에서 스트리트 댄스 챌린지. 구경꾼들 반응 담기 목표!',
       '홍대 놀이터',
       '댄스', 5, 1, 'open',
       NOW() + INTERVAL '9 days',
       'https://cdn.example.com/audio/street.mp3',
       ARRAY['#홍대', '#스트리트댄스', '#퍼포먼스', '#댄스챌린지']
FROM users u WHERE u.email = 'evan@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '제주도 해변 스노클링 릴스',
       '제주 협재해변에서 스노클링 + 물속 촬영 챌린지. 수중 카메라 가져오세요',
       '제주 협재해변',
       '스포츠', 4, 1, 'open',
       NOW() + INTERVAL '14 days',
       'https://cdn.example.com/audio/ocean.mp3',
       ARRAY['#제주도', '#스노클링', '#협재해변', '#수중촬영']
FROM users u WHERE u.email = 'alice@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '압구정 로데오 패션 챌린지',
       '압구정 로데오에서 각자 개성 있는 패션으로 나타나서 릴스 찍기. 콘셉트는 자유!',
       '압구정 로데오',
       '일상', 3, 0, 'open',
       NOW() + INTERVAL '10 days',
       'https://cdn.example.com/audio/fashion.mp3',
       ARRAY['#압구정', '#패션', '#로데오', '#스트릿패션']
FROM users u WHERE u.email = 'bob@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '신촌 이자카야 먹방 투어',
       '신촌 이자카야 거리 3군데 투어하면서 음식 릴스. 주류 포함이니 성인만',
       '신촌',
       '푸드', 4, 0, 'open',
       NOW() + INTERVAL '5 days',
       'https://cdn.example.com/audio/izakaya.mp3',
       ARRAY['#신촌', '#이자카야', '#먹방', '#푸드릴스']
FROM users u WHERE u.email = 'charlie@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '인사동 전통 디저트 챌린지',
       '인사동 전통 디저트(꽈배기, 호떡, 대추차) 먹방 릴스. 한복 입으면 이쁠 듯!',
       '인사동',
       '푸드', 3, 0, 'open',
       NOW() + INTERVAL '6 days',
       'https://cdn.example.com/audio/traditional.mp3',
       ARRAY['#인사동', '#전통디저트', '#한복', '#푸드']
FROM users u WHERE u.email = 'diana@example.com';

INSERT INTO challenges
  (host_user_id, title, description, location_text, category,
   max_participants, current_participants, status, deadline_at, audio_url, hashtags)
SELECT u.id,
       '새벽 4시 한강 라이딩 (기타)',
       '한강 자전거 도로 야간 라이딩 후 일출 감상. 자전거 필수, 헬멧 착용 필수',
       '반포한강공원',
       '기타', 4, 0, 'open',
       NOW() + INTERVAL '3 days',
       'https://cdn.example.com/audio/night_ride.mp3',
       ARRAY['#한강라이딩', '#새벽', '#자전거', '#기타챌린지']
FROM users u WHERE u.email = 'evan@example.com';

-- ── 참여 이력 10건 (alice 기준 개인화 스코어링 검증) ─────
-- alice: 댄스 4회 + 스포츠 2회 → 댄스 카테고리 최상위 노출 기대

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = 'APT 챌린지 모집 중' AND u.email = 'alice@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '정국 Seven 챌린지 크루 구함' AND u.email = 'alice@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '뉴진스 Hype Boy 같이 해요' AND u.email = 'alice@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '아이브 Eleven 챌린지' AND u.email = 'alice@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '마라톤 훈련 크루 모집' AND u.email = 'alice@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '농구 챌린지 (3점슛 성공 릴스)' AND u.email = 'alice@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '광장시장 먹방 챌린지' AND u.email = 'bob@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '성수동 카페 투어 브이로그' AND u.email = 'charlie@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '남산 타워 야경 감성 릴스' AND u.email = 'evan@example.com';

INSERT INTO participations (challenge_id, applicant_user_id, status)
SELECT c.id, u.id, 'accepted'
FROM challenges c, users u
WHERE c.title = '서울 한강 일출 촬영 같이 해요' AND u.email = 'evan@example.com';
