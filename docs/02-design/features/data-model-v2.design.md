# 데이터 모델 설계 v2.0 (확장판)

## 문서 정보

- **Feature**: 전체 데이터 모델 설계
- **Phase**: Design
- **작성일**: 2026-02-03
- **상태**: In Progress
- **버전**: 2.0 (기존 홈페이지 분석 기반 확장)

## 개요

성복교회 기존 홈페이지(sungbok.or.kr)를 분석하여 실제 교회 홈페이지에 필요한 전체 메뉴와 기능을 반영한 데이터 모델입니다.

## 메뉴 구조 (Site Map)

```
성복교회 홈페이지
├── 교회소개
│   ├── 인사말
│   ├── 담임목사 소개
│   ├── 교회 연혁
│   ├── 섬기는 이들 (교역자, 장로, 권사, 집사)
│   └── 오시는 길
│
├── 예배안내
│   ├── 예배 시간 안내
│   └── 온라인 예배 (실시간 스트리밍)
│
├── 말씀과 찬양
│   ├── 주일설교 (영상 아카이브)
│   ├── 수요설교
│   └── 찬양 영상
│
├── 교육/양육
│   ├── 주일학교 (유치부, 유년부, 초등부, 중고등부)
│   ├── 청년부
│   ├── 장년부
│   ├── 새가족반
│   └── 성경공부/제자훈련
│
├── 선교/봉사
│   ├── 국내선교
│   ├── 해외선교
│   └── 사회봉사
│
├── 나눔터
│   ├── 공지사항
│   ├── 주보 (PDF)
│   ├── 사진첩 (갤러리)
│   ├── 영상갤러리
│   ├── 간증
│   └── 기도요청
│
└── 헌금안내
    ├── 헌금 계좌
    └── 온라인 헌금
```

---

## 도메인 모델 (확장)

### 📌 1. 교회소개 도메인

#### Entity: Page (정적 페이지)
인사말, 교회 연혁, 오시는 길 등의 정적 콘텐츠

**필드**:
- `id` (Long, PK)
- `slug` (String, 100, Unique): URL 슬러그 (예: "greeting", "history")
- `title` (String, 200): 페이지 제목
- `content` (Text): 내용 (HTML)
- `metaDescription` (String, 300): SEO 메타 설명
- `isPublished` (Boolean): 공개 여부
- `displayOrder` (Integer): 표시 순서
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**예시 데이터**:
- slug: "greeting" → 인사말
- slug: "history" → 교회 연혁
- slug: "location" → 오시는 길

---

#### Entity: Pastor (담임목사/교역자)
목사 및 교역자 정보

**필드**:
- `id` (Long, PK)
- `name` (String, 50): 이름
- `position` (String, 50): 직책 (예: "담임목사", "부목사", "전도사")
- `photo` (String, 500): 사진 URL
- `education` (Text): 학력
- `career` (Text): 경력
- `message` (Text): 인사말
- `email` (String, 100): 이메일
- `phone` (String, 20): 전화번호
- `displayOrder` (Integer): 표시 순서
- `isActive` (Boolean): 재직 여부
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

#### Entity: Staff (섬기는 이들)
장로, 권사, 집사 등 교회 임직자

**필드**:
- `id` (Long, PK)
- `name` (String, 50): 이름
- `role` (Enum: StaffRole): 직분 (ELDER, EXHORTER, DEACON, DEACONESS)
- `department` (String, 100): 담당 부서
- `photo` (String, 500): 사진 URL
- `displayOrder` (Integer): 표시 순서
- `isActive` (Boolean): 활동 여부
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Enum: StaffRole**
```java
public enum StaffRole {
    ELDER("장로"),
    EXHORTER("권사"),
    DEACON("집사"),
    DEACONESS("안수집사");
}
```

---

### 📌 2. 예배안내 도메인

#### Entity: Worship (예배)
v1과 동일하지만 `liveStreamUrl` 필드 추가

**필드**:
- `id` (Long, PK)
- `type` (Enum: WorshipType)
- `title` (String, 100)
- `description` (Text)
- `dayOfWeek` (Enum: DayOfWeek)
- `startTime` (LocalTime)
- `location` (String, 100)
- `liveStreamUrl` (String, 500): **NEW** - 실시간 스트리밍 URL
- `isLiveNow` (Boolean): **NEW** - 현재 라이브 중 여부
- `isActive` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

#### Entity: YouTubeLive (유튜브 라이브)
실시간 유튜브 스트리밍 정보

**필드**:
- `id` (Long, PK)
- `worshipId` (Long, FK, Nullable): 연관된 예배
- `youtubeVideoId` (String, 50): YouTube 영상 ID
- `title` (String, 200): 라이브 제목
- `scheduledStartTime` (LocalDateTime): 예정 시작 시간
- `actualStartTime` (LocalDateTime, Nullable): 실제 시작 시간
- `endTime` (LocalDateTime, Nullable): 종료 시간
- `status` (Enum: LiveStatus): SCHEDULED, LIVE, ENDED
- `viewerCount` (Integer): 현재 시청자 수
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Enum: LiveStatus**
```java
public enum LiveStatus {
    SCHEDULED("예정"),
    LIVE("방송중"),
    ENDED("종료");
}
```

---

### 📌 3. 말씀과 찬양 도메인

#### Entity: Sermon (설교)
v1 확장 - YouTube 재생목록, 자동 업데이트 지원

**필드**:
- `id` (Long, PK)
- `worshipId` (Long, FK)
- `title` (String, 200)
- `bibleVerse` (String, 100)
- `preacher` (String, 50)
- `sermonDate` (LocalDate)
- `youtubeVideoId` (String, 50): **NEW** - YouTube 영상 ID
- `videoUrl` (String, 500): 전체 URL
- `thumbnailUrl` (String, 500)
- `duration` (Integer): 초 단위
- `viewCount` (Integer)
- `description` (Text)
- `tags` (String, 500): **NEW** - 태그 (쉼표 구분)
- `isPublished` (Boolean)
- `isFeatured` (Boolean): **NEW** - 추천 설교 여부
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**인덱스**:
- `idx_sermon_date` (sermonDate DESC)
- `idx_featured` (isFeatured, sermonDate DESC)
- `idx_tags` (tags) - Full-text search

---

#### Entity: YouTubePlaylist (재생목록)
YouTube 재생목록 관리

**필드**:
- `id` (Long, PK)
- `playlistId` (String, 50): YouTube 재생목록 ID
- `title` (String, 200): 재생목록 제목
- `description` (Text)
- `thumbnailUrl` (String, 500)
- `videoCount` (Integer): 영상 개수
- `category` (String, 50): 카테고리 (주일설교, 수요설교, 찬양 등)
- `lastSyncedAt` (LocalDateTime): **NEW** - 마지막 동기화 시간
- `isActive` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

#### Entity: Hymn (찬양)
찬양 영상 및 정보

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 찬양 제목
- `artist` (String, 100): 찬양팀/성가대
- `youtubeVideoId` (String, 50)
- `videoUrl` (String, 500)
- `thumbnailUrl` (String, 500)
- `performanceDate` (LocalDate): 찬양 날짜
- `viewCount` (Integer)
- `isPublished` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

### 📌 4. 교육/양육 도메인

#### Entity: Ministry (부서/교육)
주일학교, 청년부, 장년부 등 교육 부서

**필드**:
- `id` (Long, PK)
- `name` (String, 100): 부서명 (예: "유치부", "청년부")
- `category` (Enum: MinistryCategory): 카테고리
- `description` (Text): 부서 소개
- `targetAge` (String, 50): 대상 연령 (예: "7-13세")
- `schedule` (String, 200): 모임 시간 (예: "주일 오전 11시")
- `location` (String, 100): 모임 장소
- `leader` (String, 50): 담당 교역자
- `contact` (String, 100): 연락처
- `photoUrl` (String, 500): 대표 이미지
- `isActive` (Boolean)
- `displayOrder` (Integer)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Enum: MinistryCategory**
```java
public enum MinistryCategory {
    SUNDAY_SCHOOL("주일학교"),
    YOUTH("청년부"),
    ADULT("장년부"),
    SENIOR("경로부"),
    NEWCOMER("새가족"),
    BIBLE_STUDY("성경공부"),
    DISCIPLE_TRAINING("제자훈련");
}
```

---

### 📌 5. 선교/봉사 도메인

#### Entity: Mission (선교)
국내/해외 선교 활동

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 선교 제목
- `type` (Enum: MissionType): DOMESTIC, OVERSEAS
- `country` (String, 100): 국가 (해외선교)
- `region` (String, 100): 지역
- `description` (Text): 선교 내용
- `missionaryName` (String, 50): 선교사 이름
- `startDate` (LocalDate): 선교 시작일
- `endDate` (LocalDate, Nullable): 종료일
- `supportAmount` (Long): 후원 금액
- `photoUrl` (String, 500): 사진
- `isActive` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Enum: MissionType**
```java
public enum MissionType {
    DOMESTIC("국내선교"),
    OVERSEAS("해외선교"),
    SOCIAL_SERVICE("사회봉사");
}
```

---

### 📌 6. 나눔터 도메인

#### Entity: Notice (공지사항)
v1과 동일

---

#### Entity: Bulletin (주보)
주간 주보 PDF

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 주보 제목 (예: "2026년 2월 3일 주보")
- `bulletinDate` (LocalDate): 주보 날짜
- `pdfUrl` (String, 500): PDF 파일 URL
- `fileSize` (Long): 파일 크기 (bytes)
- `thumbnailUrl` (String, 500): 썸네일 이미지
- `downloadCount` (Integer): 다운로드 수
- `isPublished` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**인덱스**:
- `idx_bulletin_date` (bulletinDate DESC)

---

#### Entity: Gallery (사진첩/갤러리)
교회 행사 사진

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 앨범 제목
- `description` (Text): 설명
- `eventDate` (LocalDate): 행사 날짜
- `coverImageUrl` (String, 500): 대표 이미지
- `viewCount` (Integer)
- `isPublished` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**관계**:
- One-to-Many → GalleryImage

---

#### Entity: GalleryImage (갤러리 이미지)
갤러리 앨범의 개별 사진

**필드**:
- `id` (Long, PK)
- `galleryId` (Long, FK)
- `imageUrl` (String, 500): 이미지 URL
- `thumbnailUrl` (String, 500): 썸네일 URL
- `caption` (String, 500): 설명
- `fileSize` (Long): 파일 크기
- `width` (Integer): 이미지 너비
- `height` (Integer): 이미지 높이
- `displayOrder` (Integer): 표시 순서
- `createdAt` (LocalDateTime)

---

#### Entity: VideoGallery (영상 갤러리)
행사 영상 갤러리

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 영상 제목
- `description` (Text)
- `youtubeVideoId` (String, 50)
- `videoUrl` (String, 500)
- `thumbnailUrl` (String, 500)
- `eventDate` (LocalDate): 행사 날짜
- `category` (String, 50): 카테고리 (행사, 특송, 간증 등)
- `viewCount` (Integer)
- `isPublished` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

#### Entity: Testimony (간증)
교인 간증

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 간증 제목
- `author` (String, 50): 작성자
- `content` (Text): 간증 내용 (HTML)
- `category` (String, 50): 카테고리 (치유, 응답, 감사 등)
- `isApproved` (Boolean): 승인 여부
- `viewCount` (Integer)
- `publishedAt` (LocalDateTime)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

#### Entity: PrayerRequest (기도요청)
교인 기도요청

**필드**:
- `id` (Long, PK)
- `title` (String, 200): 제목
- `content` (Text): 기도 제목
- `requester` (String, 50): 요청자 (익명 가능)
- `isAnonymous` (Boolean): 익명 여부
- `isApproved` (Boolean): 승인 여부
- `status` (Enum: PrayerStatus): PENDING, APPROVED, ANSWERED
- `answeredAt` (LocalDateTime, Nullable): 응답 날짜
- `prayerCount` (Integer): 기도한 사람 수
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

**Enum: PrayerStatus**
```java
public enum PrayerStatus {
    PENDING("대기"),
    APPROVED("승인"),
    ANSWERED("응답됨");
}
```

---

### 📌 7. 헌금안내 도메인

#### Entity: DonationAccount (헌금 계좌)
헌금 계좌 정보

**필드**:
- `id` (Long, PK)
- `bankName` (String, 50): 은행명
- `accountNumber` (String, 50): 계좌번호
- `accountHolder` (String, 50): 예금주
- `donationType` (String, 50): 헌금 종류 (십일조, 감사헌금, 건축헌금 등)
- `description` (Text): 설명
- `displayOrder` (Integer)
- `isActive` (Boolean)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)

---

### 📌 8. 기존 도메인 (v1에서 유지)

- **Event** (행사 일정) - v1과 동일
- **NoticeAttachment** (공지사항 첨부파일) - v1과 동일

---

## ERD (Entity Relationship Diagram) - 확장판

```
교회소개 도메인
┌──────────┐     ┌──────────┐     ┌──────────┐
│   Page   │     │  Pastor  │     │  Staff   │
└──────────┘     └──────────┘     └──────────┘

예배안내 도메인
┌───────────────┐
│   Worship     │
├───────────────┤
│ liveStreamUrl │ (NEW)
│ isLiveNow     │ (NEW)
└───────────────┘
       │ 1
       │
       │ N
       ▼
┌──────────────────┐
│  YouTubeLive     │ (NEW)
├──────────────────┤
│ youtubeVideoId   │
│ status           │
│ viewerCount      │
└──────────────────┘

말씀과 찬양 도메인
┌────────────────┐     ┌────────────────────┐     ┌──────────┐
│    Sermon      │     │ YouTubePlaylist    │     │   Hymn   │
├────────────────┤     ├────────────────────┤     └──────────┘
│ youtubeVideoId │     │ lastSyncedAt       │ (NEW)
│ tags           │ (NEW)│ videoCount         │
│ isFeatured     │ (NEW)└────────────────────┘
└────────────────┘

교육/양육 도메인
┌──────────────┐
│   Ministry   │ (NEW)
├──────────────┤
│ category     │
│ targetAge    │
│ leader       │
└──────────────┘

선교/봉사 도메인
┌──────────────┐
│   Mission    │ (NEW)
├──────────────┤
│ type         │
│ missionaryName│
└──────────────┘

나눔터 도메인
┌──────────┐     ┌──────────┐     ┌───────────────┐
│  Notice  │     │ Bulletin │ (NEW)│   Gallery     │ (NEW)
└──────────┘     └──────────┘     └───────────────┘
                                          │ 1
                                          │
                                          │ N
                                          ▼
                                   ┌──────────────┐
                                   │ GalleryImage │ (NEW)
                                   └──────────────┘

┌──────────────────┐     ┌──────────────┐     ┌──────────────────┐
│  VideoGallery    │ (NEW)│  Testimony   │ (NEW)│  PrayerRequest   │ (NEW)
└──────────────────┘     └──────────────┘     └──────────────────┘

헌금안내 도메인
┌────────────────────┐
│  DonationAccount   │ (NEW)
└────────────────────┘
```

---

## YouTube API 연동 전략

### 1. YouTube Data API v3 사용

**필요한 기능**:
- 채널의 최신 영상 목록 가져오기
- 재생목록 영상 가져오기
- 라이브 스트리밍 상태 확인
- 영상 메타데이터 (제목, 설명, 썸네일, 조회수 등)

### 2. 실시간 라이브 스트리밍 구현

**Backend**: Spring Scheduled Task로 주기적 확인
```java
@Scheduled(fixedRate = 60000) // 1분마다
public void checkLiveStatus() {
    // YouTube API로 라이브 상태 확인
    // YouTubeLive 엔티티 업데이트
}
```

**Frontend**: 실시간 스트리밍 임베드
```tsx
// 라이브 중일 때 자동으로 표시
{isLiveNow && (
  <iframe
    src={`https://www.youtube.com/embed/${youtubeVideoId}?autoplay=1`}
    allow="accelerometer; autoplay; encrypted-media"
  />
)}
```

### 3. 최신 영상 자동 업데이트

**Cron Job**: 매일 자동으로 최신 영상 가져오기
```java
@Scheduled(cron = "0 0 * * * *") // 매시간
public void syncLatestVideos() {
    // YouTube API: channels.list → playlistId
    // YouTube API: playlistItems.list → 최신 영상
    // Sermon 엔티티에 저장
}
```

### 4. 재생목록 임베드

**Frontend**: YouTube 재생목록 임베드
```tsx
<iframe
  src={`https://www.youtube.com/embed/videoseries?list=${playlistId}`}
/>
```

---

## API 엔드포인트 매핑 (확장)

### Church (교회소개)
- `GET /api/pages/{slug}` - 정적 페이지 조회
- `GET /api/pastors` - 교역자 목록
- `GET /api/staff` - 섬기는 이들 목록

### Worship (예배안내)
- `GET /api/worships` - 예배 시간 목록
- `GET /api/worships/live` - **NEW** - 현재 라이브 방송 정보
- `GET /api/youtube/live/status` - **NEW** - 라이브 상태 확인

### Sermon (말씀과 찬양)
- `GET /api/sermons` - 설교 목록 (페이징, 필터링)
- `GET /api/sermons/featured` - **NEW** - 추천 설교
- `GET /api/sermons/latest` - 최신 설교
- `GET /api/youtube/playlists` - **NEW** - 재생목록 목록
- `GET /api/youtube/playlists/{id}/videos` - **NEW** - 재생목록 영상
- `GET /api/hymns` - **NEW** - 찬양 목록

### Ministry (교육/양육)
- `GET /api/ministries` - **NEW** - 부서 목록
- `GET /api/ministries/{id}` - **NEW** - 부서 상세

### Mission (선교/봉사)
- `GET /api/missions` - **NEW** - 선교 목록
- `GET /api/missions/{type}` - **NEW** - 선교 타입별 조회

### Community (나눔터)
- `GET /api/notices` - 공지사항 목록
- `GET /api/bulletins` - **NEW** - 주보 목록
- `GET /api/bulletins/latest` - **NEW** - 최신 주보
- `GET /api/galleries` - **NEW** - 사진첩 목록
- `GET /api/galleries/{id}/images` - **NEW** - 갤러리 이미지
- `GET /api/video-galleries` - **NEW** - 영상 갤러리
- `GET /api/testimonies` - **NEW** - 간증 목록
- `GET /api/prayer-requests` - **NEW** - 기도요청 목록
- `POST /api/prayer-requests` - **NEW** - 기도요청 등록
- `POST /api/prayer-requests/{id}/pray` - **NEW** - 기도하기

### Donation (헌금)
- `GET /api/donation-accounts` - **NEW** - 헌금 계좌 목록

---

## TypeScript 타입 정의 (확장)

```typescript
// types/church.ts
export interface Page {
  id: number;
  slug: string;
  title: string;
  content: string;
  metaDescription: string;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Pastor {
  id: number;
  name: string;
  position: string;
  photo?: string;
  education?: string;
  career?: string;
  message?: string;
  email?: string;
  phone?: string;
  displayOrder: number;
  isActive: boolean;
}

export interface Staff {
  id: number;
  name: string;
  role: 'ELDER' | 'EXHORTER' | 'DEACON' | 'DEACONESS';
  department?: string;
  photo?: string;
  displayOrder: number;
  isActive: boolean;
}

// types/youtube.ts
export interface YouTubeLive {
  id: number;
  worshipId?: number;
  youtubeVideoId: string;
  title: string;
  scheduledStartTime: string;
  actualStartTime?: string;
  endTime?: string;
  status: 'SCHEDULED' | 'LIVE' | 'ENDED';
  viewerCount: number;
}

export interface YouTubePlaylist {
  id: number;
  playlistId: string;
  title: string;
  description?: string;
  thumbnailUrl?: string;
  videoCount: number;
  category: string;
  lastSyncedAt: string;
  isActive: boolean;
}

// types/ministry.ts
export interface Ministry {
  id: number;
  name: string;
  category: 'SUNDAY_SCHOOL' | 'YOUTH' | 'ADULT' | 'SENIOR' | 'NEWCOMER' | 'BIBLE_STUDY';
  description?: string;
  targetAge?: string;
  schedule?: string;
  location?: string;
  leader?: string;
  contact?: string;
  photoUrl?: string;
  isActive: boolean;
  displayOrder: number;
}

// types/mission.ts
export interface Mission {
  id: number;
  title: string;
  type: 'DOMESTIC' | 'OVERSEAS' | 'SOCIAL_SERVICE';
  country?: string;
  region?: string;
  description?: string;
  missionaryName?: string;
  startDate: string;
  endDate?: string;
  supportAmount?: number;
  photoUrl?: string;
  isActive: boolean;
}

// types/gallery.ts
export interface Gallery {
  id: number;
  title: string;
  description?: string;
  eventDate: string;
  coverImageUrl?: string;
  viewCount: number;
  isPublished: boolean;
  createdAt: string;
  images?: GalleryImage[];
}

export interface GalleryImage {
  id: number;
  galleryId: number;
  imageUrl: string;
  thumbnailUrl?: string;
  caption?: string;
  fileSize: number;
  width: number;
  height: number;
  displayOrder: number;
}

export interface Bulletin {
  id: number;
  title: string;
  bulletinDate: string;
  pdfUrl: string;
  fileSize: number;
  thumbnailUrl?: string;
  downloadCount: number;
  isPublished: boolean;
}

export interface Testimony {
  id: number;
  title: string;
  author: string;
  content: string;
  category: string;
  isApproved: boolean;
  viewCount: number;
  publishedAt: string;
}

export interface PrayerRequest {
  id: number;
  title: string;
  content: string;
  requester: string;
  isAnonymous: boolean;
  isApproved: boolean;
  status: 'PENDING' | 'APPROVED' | 'ANSWERED';
  answeredAt?: string;
  prayerCount: number;
  createdAt: string;
}
```

---

## 초기 데이터 (Seed Data) - 확장

### Pages (정적 페이지)
```sql
INSERT INTO page (slug, title, content, meta_description, is_published, display_order) VALUES
('greeting', '인사말', '<h1>성복교회에 오신 것을 환영합니다</h1>...', '담임목사 인사말', true, 1),
('history', '교회 연혁', '<ul><li>1990년: 교회 설립</li>...', '성복교회 연혁', true, 2),
('location', '오시는 길', '<p>주소: 서울특별시...</p>...', '성복교회 위치 안내', true, 3);
```

### YouTube Playlists
```sql
INSERT INTO youtube_playlist (playlist_id, title, category, video_count, is_active) VALUES
('PLxxxxxx', '주일 설교', '주일설교', 0, true),
('PLyyyyyy', '수요 예배', '수요예배', 0, true),
('PLzzzzzz', '특별 집회', '특별집회', 0, true);
```

---

## 마이그레이션 전략 (확장)

### Migration Scripts
```
V1__create_worship_tables.sql
V2__create_sermon_tables.sql
V3__create_notice_tables.sql
V4__create_event_tables.sql
V5__create_church_info_tables.sql      (NEW - Page, Pastor, Staff)
V6__create_youtube_tables.sql          (NEW - YouTubeLive, YouTubePlaylist)
V7__create_ministry_tables.sql         (NEW - Ministry)
V8__create_mission_tables.sql          (NEW - Mission)
V9__create_gallery_tables.sql          (NEW - Gallery, GalleryImage, VideoGallery)
V10__create_bulletin_tables.sql        (NEW - Bulletin)
V11__create_testimony_tables.sql       (NEW - Testimony, PrayerRequest)
V12__create_donation_tables.sql        (NEW - DonationAccount)
V13__insert_initial_data.sql
```

---

## 파일 스토리지 전략

### OCI Object Storage 활용

**저장 경로 구조**:
```
sungbok-church-storage/
├── images/
│   ├── pastor/          # 교역자 사진
│   ├── staff/           # 섬기는 이들 사진
│   ├── gallery/         # 갤러리 이미지
│   └── thumbnail/       # 썸네일
├── files/
│   ├── bulletin/        # 주보 PDF
│   └── attachments/     # 공지사항 첨부파일
└── videos/              # 로컬 영상 (선택사항)
```

---

## 성능 최적화 전략

### 1. 인덱스 전략
- `sermon.sermon_date DESC, is_featured`
- `gallery.event_date DESC`
- `bulletin.bulletin_date DESC`
- `prayer_request.status, created_at DESC`

### 2. 캐싱 전략 (Redis)
- 라이브 방송 상태 (TTL: 1분)
- 최신 설교 목록 (TTL: 1시간)
- 주보 목록 (TTL: 1일)
- YouTube 재생목록 (TTL: 6시간)

### 3. CDN 활용
- 이미지, 썸네일 → CDN 캐싱
- 주보 PDF → CDN 캐싱

---

## 다음 단계

1. ✅ 확장된 데이터 모델 설계 완료
2. ⬜ **확장된 API 명세 작성**
3. ⬜ **YouTube API 연동 가이드 작성**
4. ⬜ **Backend Entity 구현 (Phase 4)**
5. ⬜ **Frontend 컴포넌트 설계 (Phase 5-6)**

---

**작성일**: 2026-02-03
**작성자**: Claude Code
**문서 버전**: 2.0 (확장판)
**상태**: Design Complete
