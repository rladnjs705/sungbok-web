# Backend 구현 계획

## 현재 진행 상황

### ✅ 완료된 작업

1. **프로젝트 구조**
   - ✅ `pom.xml` (Maven 의존성)
   - ✅ `application.yml` (설정)
   - ✅ `SungbokChurchApplication.java` (메인 클래스)

2. **공통 클래스**
   - ✅ `BaseEntity` (id, createdAt, updatedAt)
   - ✅ `JpaConfig` (JPA Auditing)

3. **Enum 클래스** (3개)
   - ✅ `WorshipType` (예배 유형)
   - ✅ `NoticeCategory` (공지사항 카테고리)
   - ✅ `LiveStatus` (라이브 상태)

4. **Entity 클래스** (4개)
   - ✅ `Worship` (예배)
   - ✅ `Sermon` (설교)
   - ✅ `Notice` (공지사항)
   - ✅ `YouTubeLive` (라이브)

---

## 전체 구현 계획

### 📋 Phase 1: Entity Layer (17개 Entity)

#### 완료 (4개)
- ✅ Worship
- ✅ Sermon
- ✅ Notice
- ✅ YouTubeLive

#### 진행 중 (13개)
- ⬜ Page (정적 페이지)
- ⬜ Pastor (담임목사/교역자)
- ⬜ Staff (섬기는 이들)
- ⬜ YouTubePlaylist (재생목록)
- ⬜ Hymn (찬양)
- ⬜ Ministry (교육/양육 부서)
- ⬜ Mission (선교)
- ⬜ NoticeAttachment (공지사항 첨부파일)
- ⬜ Bulletin (주보)
- ⬜ Gallery (사진첩)
- ⬜ GalleryImage (갤러리 이미지)
- ⬜ VideoGallery (영상갤러리)
- ⬜ Testimony (간증)
- ⬜ PrayerRequest (기도요청)
- ⬜ DonationAccount (헌금 계좌)
- ⬜ Event (행사 일정)

---

### 📋 Phase 2: Repository Layer (17개 Repository)

Spring Data JPA Repository 인터페이스 생성

```java
public interface WorshipRepository extends JpaRepository<Worship, Long> {
    List<Worship> findByIsActiveTrueOrderByDayOfWeekAsc();
    Optional<Worship> findByIsLiveNowTrue();
}
```

---

### 📋 Phase 3: DTO Layer

**Request DTO**: API 요청 데이터
**Response DTO**: API 응답 데이터

예시:
- `SermonRequest.java`
- `SermonResponse.java`
- `NoticeRequest.java`
- `NoticeResponse.java`

---

### 📋 Phase 4: Service Layer

비즈니스 로직 구현

주요 서비스:
- `WorshipService`
- `SermonService`
- `NoticeService`
- `YouTubeService` (YouTube API 연동)
- `YouTubeScheduler` (자동화)

---

### 📋 Phase 5: Controller Layer

REST API 엔드포인트 구현

주요 컨트롤러:
- `WorshipController`
- `SermonController`
- `NoticeController`
- `YouTubeController`

---

### 📋 Phase 6: YouTube API 연동

- `YouTubeApiClient` (YouTube API 호출)
- `YouTubeService` (비즈니스 로직)
- `YouTubeScheduler` (자동화)
  - 1분마다: 라이브 상태 확인
  - 매시간: 최신 영상 동기화
  - 6시간마다: 재생목록 동기화

---

### 📋 Phase 7: 예외 처리 & 공통 응답

- `GlobalExceptionHandler`
- `ApiResponse<T>` (통일된 응답 형식)
- `ErrorResponse` (에러 응답)

---

### 📋 Phase 8: Database Migration

Flyway 마이그레이션 스크립트

```
V1__create_worship_tables.sql
V2__create_sermon_tables.sql
V3__create_notice_tables.sql
...
V13__insert_initial_data.sql
```

---

## 예상 작업량

| Phase | 파일 수 | 예상 시간 |
|-------|---------|----------|
| Entity (13개 추가) | 13 | 30분 |
| Enum (추가) | 5 | 10분 |
| Repository | 17 | 20분 |
| DTO | 34 | 40분 |
| Service | 10 | 1시간 |
| Controller | 10 | 1시간 |
| YouTube API | 3 | 30분 |
| 예외 처리 | 3 | 20분 |
| Migration | 13 | 30분 |
| **총 예상 시간** | **~100파일** | **~5시간** |

---

## 우선순위 구현 순서

### 🔥 High Priority (즉시 필요)
1. **Worship, Sermon, Notice** (이미 완료)
2. **YouTubeLive** (이미 완료)
3. **Repository 4개** (CRUD 기능)
4. **Service 4개** (비즈니스 로직)
5. **Controller 4개** (API 엔드포인트)
6. **YouTube API 연동** (라이브 & 동기화)

### 🟡 Medium Priority (다음 단계)
7. **Bulletin** (주보)
8. **Gallery, GalleryImage** (사진첩)
9. **Page, Pastor, Staff** (교회소개)
10. **YouTubePlaylist, Hymn** (재생목록, 찬양)

### 🟢 Low Priority (나중에)
11. **Ministry, Mission** (교육/양육, 선교)
12. **Testimony, PrayerRequest** (간증, 기도요청)
13. **DonationAccount, Event** (헌금, 행사)

---

## 다음 단계 선택지

### Option 1: 나머지 Entity 모두 생성 (추천)
→ 13개 Entity 생성
→ 데이터베이스 구조 완성

### Option 2: High Priority부터 완전 구현
→ Worship/Sermon/Notice/YouTubeLive
→ Repository → Service → Controller → YouTube API
→ 동작하는 API 먼저 완성

### Option 3: 단계별 진행
→ Phase 1 완료 (Entity 전체)
→ Phase 2 완료 (Repository 전체)
→ Phase 3 완료 (DTO 전체)
→ ...

---

**추천**: Option 2 (High Priority 완전 구현)
- 빠르게 동작하는 API 완성
- 테스트 가능
- 나머지는 필요할 때 추가

---

어떤 방식으로 진행하시겠습니까?
