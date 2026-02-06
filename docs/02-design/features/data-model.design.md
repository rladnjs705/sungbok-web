# 데이터 모델 설계

## 문서 정보

- **Feature**: 데이터 모델 설계
- **Phase**: Design
- **작성일**: 2026-02-03
- **상태**: In Progress

## 개요

성복교회 홈페이지의 핵심 기능인 **예배 안내 및 설교 영상**, **교회 소식 및 공지사항**을 지원하는 데이터 모델을 설계합니다.

## 도메인 모델

### 1. 예배 (Worship)

#### Entity: Worship
주일예배, 수요예배 등의 정기 예배 정보

**필드**:
- `id` (Long, PK): 예배 ID
- `type` (Enum: WorshipType): 예배 유형 (SUNDAY, WEDNESDAY, SPECIAL)
- `title` (String, 100): 예배 제목 (예: "주일 1부 예배")
- `description` (Text): 예배 설명
- `dayOfWeek` (Enum: DayOfWeek): 요일
- `startTime` (LocalTime): 시작 시간
- `location` (String, 100): 장소 (예: "본당")
- `isActive` (Boolean): 활성화 여부
- `createdAt` (LocalDateTime): 생성일시
- `updatedAt` (LocalDateTime): 수정일시

**관계**:
- One-to-Many → Sermon (하나의 예배 유형에 여러 설교)

---

### 2. 설교 (Sermon)

#### Entity: Sermon
설교 영상 및 메타데이터

**필드**:
- `id` (Long, PK): 설교 ID
- `worshipId` (Long, FK): 예배 ID
- `title` (String, 200): 설교 제목
- `bibleVerse` (String, 100): 본문 (예: "요한복음 3:16")
- `preacher` (String, 50): 설교자
- `sermonDate` (LocalDate): 설교 날짜
- `videoUrl` (String, 500): 영상 URL (YouTube/Vimeo 등)
- `thumbnailUrl` (String, 500): 썸네일 이미지 URL
- `duration` (Integer): 영상 길이 (초 단위)
- `viewCount` (Integer): 조회수
- `description` (Text): 설교 요약
- `isPublished` (Boolean): 공개 여부
- `createdAt` (LocalDateTime): 생성일시
- `updatedAt` (LocalDateTime): 수정일시

**관계**:
- Many-to-One → Worship (여러 설교가 하나의 예배 유형에 속함)

**인덱스**:
- `idx_sermon_date` (sermonDate DESC): 최신 설교 조회 최적화
- `idx_worship_id` (worshipId): 예배별 설교 조회

---

### 3. 공지사항 (Notice)

#### Entity: Notice
교회 새소식 및 공지사항

**필드**:
- `id` (Long, PK): 공지사항 ID
- `category` (Enum: NoticeCategory): 카테고리 (NEWS, EVENT, ANNOUNCEMENT)
- `title` (String, 200): 제목
- `content` (Text): 내용 (HTML 지원)
- `author` (String, 50): 작성자
- `isPinned` (Boolean): 상단 고정 여부
- `viewCount` (Integer): 조회수
- `publishedAt` (LocalDateTime): 게시일시
- `createdAt` (LocalDateTime): 생성일시
- `updatedAt` (LocalDateTime): 수정일시

**관계**:
- One-to-Many → NoticeAttachment (하나의 공지사항에 여러 첨부파일)

**인덱스**:
- `idx_published_date` (publishedAt DESC): 최신 공지사항 조회
- `idx_category` (category): 카테고리별 조회

---

### 4. 공지사항 첨부파일 (NoticeAttachment)

#### Entity: NoticeAttachment
공지사항 첨부파일 (주보 등)

**필드**:
- `id` (Long, PK): 첨부파일 ID
- `noticeId` (Long, FK): 공지사항 ID
- `fileName` (String, 255): 원본 파일명
- `fileUrl` (String, 500): 파일 URL (S3/OCI Object Storage)
- `fileSize` (Long): 파일 크기 (bytes)
- `fileType` (String, 50): MIME 타입
- `createdAt` (LocalDateTime): 업로드일시

**관계**:
- Many-to-One → Notice (여러 첨부파일이 하나의 공지사항에 속함)

---

### 5. 행사 일정 (Event)

#### Entity: Event
교회 행사 및 일정

**필드**:
- `id` (Long, PK): 행사 ID
- `title` (String, 200): 행사 제목
- `description` (Text): 행사 설명
- `location` (String, 100): 장소
- `startDate` (LocalDateTime): 시작일시
- `endDate` (LocalDateTime): 종료일시
- `registrationRequired` (Boolean): 등록 필요 여부
- `maxParticipants` (Integer, Nullable): 최대 참가자 수
- `currentParticipants` (Integer): 현재 참가자 수
- `isPublished` (Boolean): 공개 여부
- `createdAt` (LocalDateTime): 생성일시
- `updatedAt` (LocalDateTime): 수정일시

**인덱스**:
- `idx_event_date` (startDate ASC): 날짜별 행사 조회

---

## ERD (Entity Relationship Diagram)

```
┌─────────────────┐
│   Worship       │
├─────────────────┤
│ id (PK)         │
│ type            │
│ title           │
│ description     │
│ dayOfWeek       │
│ startTime       │
│ location        │
│ isActive        │
│ createdAt       │
│ updatedAt       │
└─────────────────┘
         │
         │ 1
         │
         │ N
         ▼
┌─────────────────┐
│   Sermon        │
├─────────────────┤
│ id (PK)         │
│ worshipId (FK)  │────┐
│ title           │    │
│ bibleVerse      │    │
│ preacher        │    │
│ sermonDate      │    │
│ videoUrl        │    │
│ thumbnailUrl    │    │
│ duration        │    │
│ viewCount       │    │
│ description     │    │
│ isPublished     │    │
│ createdAt       │    │
│ updatedAt       │    │
└─────────────────┘    │
                       │
                       └──────┐
                              │
┌─────────────────┐          │
│   Notice        │          │
├─────────────────┤          │
│ id (PK)         │          │
│ category        │          │
│ title           │          │
│ content         │          │
│ author          │          │
│ isPinned        │          │
│ viewCount       │          │
│ publishedAt     │          │
│ createdAt       │          │
│ updatedAt       │          │
└─────────────────┘          │
         │                   │
         │ 1                 │
         │                   │
         │ N                 │
         ▼                   │
┌──────────────────────┐     │
│ NoticeAttachment     │     │
├──────────────────────┤     │
│ id (PK)              │     │
│ noticeId (FK)        │────┘
│ fileName             │
│ fileUrl              │
│ fileSize             │
│ fileType             │
│ createdAt            │
└──────────────────────┘


┌─────────────────┐
│   Event         │
├─────────────────┤
│ id (PK)         │
│ title           │
│ description     │
│ location        │
│ startDate       │
│ endDate         │
│ registrationReq │
│ maxParticipants │
│ currentPartici… │
│ isPublished     │
│ createdAt       │
│ updatedAt       │
└─────────────────┘
```

---

## Enum 정의

### WorshipType
```java
public enum WorshipType {
    SUNDAY("주일예배"),
    WEDNESDAY("수요예배"),
    DAWN("새벽예배"),
    SPECIAL("특별예배");

    private final String description;
}
```

### NoticeCategory
```java
public enum NoticeCategory {
    NEWS("새소식"),
    EVENT("행사안내"),
    ANNOUNCEMENT("공지사항"),
    BULLETIN("주보");

    private final String description;
}
```

---

## 데이터베이스 스키마 (PostgreSQL)

### Worship 테이블
```sql
CREATE TABLE worship (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    location VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_worship_active ON worship(is_active);
```

### Sermon 테이블
```sql
CREATE TABLE sermon (
    id BIGSERIAL PRIMARY KEY,
    worship_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    bible_verse VARCHAR(100),
    preacher VARCHAR(50) NOT NULL,
    sermon_date DATE NOT NULL,
    video_url VARCHAR(500),
    thumbnail_url VARCHAR(500),
    duration INTEGER,
    view_count INTEGER DEFAULT 0,
    description TEXT,
    is_published BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (worship_id) REFERENCES worship(id) ON DELETE CASCADE
);

CREATE INDEX idx_sermon_date ON sermon(sermon_date DESC);
CREATE INDEX idx_sermon_worship ON sermon(worship_id);
CREATE INDEX idx_sermon_published ON sermon(is_published, sermon_date DESC);
```

### Notice 테이블
```sql
CREATE TABLE notice (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(50) NOT NULL,
    is_pinned BOOLEAN DEFAULT false,
    view_count INTEGER DEFAULT 0,
    published_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notice_published ON notice(published_at DESC);
CREATE INDEX idx_notice_category ON notice(category);
CREATE INDEX idx_notice_pinned ON notice(is_pinned, published_at DESC);
```

### NoticeAttachment 테이블
```sql
CREATE TABLE notice_attachment (
    id BIGSERIAL PRIMARY KEY,
    notice_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notice_id) REFERENCES notice(id) ON DELETE CASCADE
);

CREATE INDEX idx_attachment_notice ON notice_attachment(notice_id);
```

### Event 테이블
```sql
CREATE TABLE event (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    location VARCHAR(100),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    registration_required BOOLEAN DEFAULT false,
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    is_published BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_date ON event(start_date ASC);
CREATE INDEX idx_event_published ON event(is_published, start_date ASC);
```

---

## API 엔드포인트 매핑

### Worship API
- `GET /api/worships` - 예배 목록 조회
- `GET /api/worships/{id}` - 예배 상세 조회

### Sermon API
- `GET /api/sermons` - 설교 목록 조회 (페이징, 필터링)
- `GET /api/sermons/{id}` - 설교 상세 조회
- `GET /api/sermons/latest` - 최신 설교 조회
- `POST /api/sermons/{id}/view` - 조회수 증가

### Notice API
- `GET /api/notices` - 공지사항 목록 조회 (페이징, 카테고리 필터)
- `GET /api/notices/{id}` - 공지사항 상세 조회
- `GET /api/notices/pinned` - 상단 고정 공지사항 조회
- `POST /api/notices/{id}/view` - 조회수 증가

### Event API
- `GET /api/events` - 행사 목록 조회 (날짜 범위 필터)
- `GET /api/events/{id}` - 행사 상세 조회
- `GET /api/events/upcoming` - 다가오는 행사 조회

---

## TypeScript 타입 정의 (Frontend)

```typescript
// types/worship.ts
export interface Worship {
  id: number;
  type: 'SUNDAY' | 'WEDNESDAY' | 'DAWN' | 'SPECIAL';
  title: string;
  description?: string;
  dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
  startTime: string; // "HH:mm" format
  location?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

// types/sermon.ts
export interface Sermon {
  id: number;
  worshipId: number;
  worship?: Worship; // Optional populated field
  title: string;
  bibleVerse?: string;
  preacher: string;
  sermonDate: string; // ISO date string
  videoUrl?: string;
  thumbnailUrl?: string;
  duration?: number;
  viewCount: number;
  description?: string;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

// types/notice.ts
export interface Notice {
  id: number;
  category: 'NEWS' | 'EVENT' | 'ANNOUNCEMENT' | 'BULLETIN';
  title: string;
  content: string; // HTML content
  author: string;
  isPinned: boolean;
  viewCount: number;
  publishedAt: string;
  createdAt: string;
  updatedAt: string;
  attachments?: NoticeAttachment[];
}

export interface NoticeAttachment {
  id: number;
  noticeId: number;
  fileName: string;
  fileUrl: string;
  fileSize: number;
  fileType: string;
  createdAt: string;
}

// types/event.ts
export interface Event {
  id: number;
  title: string;
  description?: string;
  location?: string;
  startDate: string;
  endDate: string;
  registrationRequired: boolean;
  maxParticipants?: number;
  currentParticipants: number;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}
```

---

## 데이터 검증 규칙

### Worship
- `type`: Required, Enum
- `title`: Required, 1-100자
- `startTime`: Required, HH:mm 형식

### Sermon
- `title`: Required, 1-200자
- `preacher`: Required, 1-50자
- `sermonDate`: Required
- `videoUrl`: Optional, URL 형식 (YouTube/Vimeo)

### Notice
- `category`: Required, Enum
- `title`: Required, 1-200자
- `content`: Required
- `author`: Required, 1-50자
- `publishedAt`: Required

### Event
- `title`: Required, 1-200자
- `startDate`: Required
- `endDate`: Required, startDate 이후
- `maxParticipants`: Optional, >= 0

---

## 마이그레이션 전략

### Initial Schema (V1)
```
V1__create_worship_tables.sql
V2__create_sermon_tables.sql
V3__create_notice_tables.sql
V4__create_event_tables.sql
V5__insert_initial_data.sql
```

### Migration Tool
- **Flyway** 사용 (Spring Boot 기본 지원)
- 버전별 스크립트 관리
- 롤백 스크립트 작성

---

## 초기 데이터 (Seed Data)

### Worship 초기 데이터
```sql
INSERT INTO worship (type, title, description, day_of_week, start_time, location, is_active) VALUES
('SUNDAY', '주일 1부 예배', '주일 오전 1부 예배', 'SUNDAY', '09:00', '본당', true),
('SUNDAY', '주일 2부 예배', '주일 오전 2부 예배', 'SUNDAY', '11:00', '본당', true),
('WEDNESDAY', '수요예배', '수요일 저녁 예배', 'WEDNESDAY', '19:30', '본당', true),
('DAWN', '새벽예배', '새벽 기도회', 'MONDAY', '05:30', '본당', true);
```

---

## 성능 최적화

### 인덱스 전략
1. **조회 빈도가 높은 컬럼**:
   - `sermon.sermon_date` (DESC): 최신 설교 조회
   - `notice.published_at` (DESC): 최신 공지사항 조회
   - `event.start_date` (ASC): 예정된 행사 조회

2. **외래키 인덱스**:
   - `sermon.worship_id`
   - `notice_attachment.notice_id`

3. **복합 인덱스**:
   - `sermon (is_published, sermon_date DESC)`: 공개된 설교 최신순
   - `notice (is_pinned, published_at DESC)`: 고정 공지사항 우선 표시

### 캐싱 전략
- **Redis 캐싱** (선택사항):
  - 최신 설교 목록 (TTL: 1시간)
  - 공지사항 목록 (TTL: 30분)
  - 예배 시간 정보 (TTL: 24시간)

---

## 다음 단계

1. ✅ 데이터 모델 설계 완료
2. ⬜ **API 명세 작성** (`docs/02-design/features/api-spec.design.md`)
3. ⬜ **Backend Entity 클래스 구현** (Phase 4)
4. ⬜ **Frontend 타입 정의 적용** (Phase 6)

---

**작성일**: 2026-02-03
**작성자**: Claude Code
**문서 버전**: 1.0
**상태**: Design Complete
