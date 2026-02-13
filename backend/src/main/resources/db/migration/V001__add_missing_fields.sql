--
-- Migration: Add missing fields for Frontend integration
-- Date: 2026-02-06
-- Description:
--   1. Notice 테이블에 excerpt, imageUrl 필드 추가
--   2. Ministry 테이블에 slug 필드 추가 및 unique 제약조건 설정
--

-- ============================================================
-- 1. Notice 테이블 수정
-- ============================================================

-- excerpt 컬럼 추가 (카드에 표시할 짧은 요약)
ALTER TABLE notice ADD COLUMN IF NOT EXISTS excerpt VARCHAR(500);
COMMENT ON COLUMN notice.excerpt IS '카드에 표시할 짧은 요약 (Frontend 목록 화면용)';

-- imageUrl 컬럼 추가 (대표 이미지 URL)
ALTER TABLE notice ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);
COMMENT ON COLUMN notice.image_url IS '대표 이미지 URL (Frontend 카드 썸네일용)';

-- ============================================================
-- 2. Ministry 테이블 수정
-- ============================================================

-- slug 컬럼 추가 (URL 라우팅용)
ALTER TABLE ministry ADD COLUMN IF NOT EXISTS slug VARCHAR(100);
COMMENT ON COLUMN ministry.slug IS 'URL 라우팅용 slug (예: youth, infant)';

-- 기존 데이터에 대한 slug 자동 생성
-- name을 소문자로 변환하고 공백을 하이픈으로 변환
UPDATE ministry
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(
            REGEXP_REPLACE(
                TRIM(name),
                '\s+', '-', 'g'                      -- 공백 -> 하이픈
            ),
            '[^a-z0-9가-힣-]', '', 'g'              -- 특수문자 제거
        ),
        '-+', '-', 'g'                               -- 연속 하이픈 제거
    )
)
WHERE slug IS NULL;

-- slug를 NOT NULL로 변경
ALTER TABLE ministry ALTER COLUMN slug SET NOT NULL;

-- slug unique 제약조건 추가
ALTER TABLE ministry ADD CONSTRAINT uk_ministry_slug UNIQUE (slug);

-- slug 인덱스 추가 (이미 unique 제약조건으로 인덱스가 생성되지만 명시적으로 추가)
CREATE INDEX IF NOT EXISTS idx_ministry_slug ON ministry(slug);

-- ============================================================
-- 검증 쿼리 (실행 후 확인용)
-- ============================================================

-- Notice 테이블 구조 확인
-- SELECT column_name, data_type, character_maximum_length, is_nullable
-- FROM information_schema.columns
-- WHERE table_name = 'notice'
-- ORDER BY ordinal_position;

-- Ministry 테이블 구조 확인
-- SELECT column_name, data_type, character_maximum_length, is_nullable
-- FROM information_schema.columns
-- WHERE table_name = 'ministry'
-- ORDER BY ordinal_position;

-- Ministry slug 중복 확인
-- SELECT slug, COUNT(*)
-- FROM ministry
-- GROUP BY slug
-- HAVING COUNT(*) > 1;
