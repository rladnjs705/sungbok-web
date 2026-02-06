# Backend API 설계

## 문서 정보

- **Feature**: Backend API (Entity, Repository, Service Layer)
- **Phase**: Design (역방향 생성 - 구현 코드 기반)
- **작성일**: 2026-02-03
- **상태**: Completed
- **버전**: 1.0

## 개요

성복교회 홈페이지의 Backend API를 Spring Boot 4.0.2 기반으로 설계 및 구현.
총 20개 Entity, 20개 Repository, 10개 Service로 구성된 완전한 데이터 레이어.

---

## 1. 도메인 모델 (Entity Layer - 20개)

### 1.1 교회 정보 도메인 (3개)

#### Page Entity
- 정적 페이지 관리 (인사말, 연혁, 오시는 길)
- **주요 필드**: slug (Unique), title, content (HTML), metaDescription, isPublished, displayOrder
- **Index**: slug (Unique), isPublished + displayOrder

#### Pastor Entity
- 담임목사 및 교역자 정보
- **주요 필드**: name, position, photo, education, career, message, email, phone
- **Index**: isActive + displayOrder

#### Staff Entity
- 섬기는 이들 (장로, 권사, 집사)
- **주요 필드**: name, role (Enum: StaffRole), department, photo
- **Index**: role, isActive + displayOrder

### 1.2 예배 도메인 (2개)

#### Worship Entity
- 예배 정보 (시간, 장소, 라이브)
- **주요 필드**: name, type (Enum: WorshipType), dayOfWeek, startTime, location, liveStreamUrl, isLiveNow
- **Unique Constraint**: type + dayOfWeek
- **Index**: isActive + dayOfWeek

#### YouTubeLive Entity
- 실시간 라이브 방송 관리
- **주요 필드**: youtubeVideoId (Unique), title, status (Enum: LiveStatus), scheduledStartTime, actualStartTime, endTime
- **Index**: youtubeVideoId (Unique), status + actualStartTime DESC

### 1.3 미디어 도메인 (3개)

#### Sermon Entity
- 설교 관리 (주일/수요설교)
- **주요 필드**: title, preacher, sermonDate, bibleVerse, youtubeVideoId, thumbnailUrl, duration, tags, isFeatured, isPublished, viewCount
- **관계**: ManyToOne → Worship
- **Index**: sermonDate DESC, worship_id, isPublished + sermonDate DESC

#### YouTubePlaylist Entity
- YouTube 재생목록 관리
- **주요 필드**: playlistId (Unique), title, description, videoCount, category, lastSyncedAt
- **Index**: playlistId (Unique), category

#### Hymn Entity
- 찬양 영상 관리
- **주요 필드**: title, artist, youtubeVideoId, performanceDate, viewCount, isPublished
- **Index**: performanceDate DESC, isPublished + performanceDate DESC

### 1.4 교육/선교 도메인 (2개)

#### Ministry Entity
- 교육/양육 부서 (주일학교, 청년부 등)
- **주요 필드**: name, category (Enum: MinistryCategory), description, targetAge, schedule, location, leader, contact
- **Index**: category, isActive + displayOrder

#### Mission Entity
- 선교 사업
- **주요 필드**: title, type (Enum: MissionType), country, region, missionaryName, startDate, endDate, supportAmount
- **Index**: type, isActive + startDate DESC

### 1.5 커뮤니티 도메인 (8개)

#### Notice Entity
- 공지사항
- **주요 필드**: category (Enum: NoticeCategory), title, content, author, isPinned, publishedAt, viewCount
- **Index**: publishedAt DESC, category + publishedAt DESC

#### NoticeAttachment Entity
- 공지사항 첨부파일
- **주요 필드**: fileName, fileUrl, fileSize, fileType
- **관계**: ManyToOne → Notice

#### Bulletin Entity
- 주보 (PDF)
- **주요 필드**: title, bulletinDate (Unique), pdfUrl, fileSize, thumbnailUrl, downloadCount
- **Index**: bulletinDate DESC, isPublished + bulletinDate DESC

#### Gallery Entity
- 사진첩
- **주요 필드**: title, description, eventDate, coverImageUrl, viewCount, isPublished
- **Index**: eventDate DESC, isPublished + eventDate DESC

#### GalleryImage Entity
- 갤러리 이미지
- **주요 필드**: imageUrl, thumbnailUrl, caption, fileSize, width, height, displayOrder
- **관계**: ManyToOne → Gallery
- **Index**: gallery_id + displayOrder

#### VideoGallery Entity
- 영상 갤러리
- **주요 필드**: title, youtubeVideoId, videoUrl, eventDate, category, viewCount, isPublished
- **Index**: eventDate DESC, isPublished + eventDate DESC, category

#### Testimony Entity
- 간증
- **주요 필드**: title, author, content, category, isApproved, publishedAt, viewCount
- **Index**: publishedAt DESC, isApproved + publishedAt DESC, category

#### PrayerRequest Entity
- 기도요청
- **주요 필드**: title, content, requester, isAnonymous, isApproved, status (Enum: PrayerStatus), answeredAt, prayerCount
- **Index**: status + createdAt DESC, isApproved + createdAt DESC

### 1.6 기타 도메인 (2개)

#### DonationAccount Entity
- 헌금 계좌
- **주요 필드**: bankName, accountNumber, accountHolder, donationType, description
- **Index**: isActive + displayOrder

#### Event Entity
- 행사 일정
- **주요 필드**: title, description, location, startDate, endDate, registrationRequired, maxParticipants, currentParticipants, isPublished
- **Index**: startDate ASC, isPublished + startDate ASC

---

## 2. Repository Layer (20개)

### 2.1 설계 원칙

✅ **Spring Data JPA 네이밍 규칙**
- `findBy...`, `existsBy...`, `countBy...` 패턴
- 메서드명으로 쿼리 의도 명확히 표현

✅ **페이징 지원**
- 목록 조회 메서드에 `Pageable` 파라미터
- `Page<Entity>` 반환 타입

✅ **커스텀 쿼리**
- 복잡한 조회는 `@Query` + JPQL
- Named parameters (`:param`) 사용

✅ **데이터 수정**
- 조회수/다운로드수 증가는 `@Modifying` + `@Query`
- 효율적인 업데이트 쿼리

### 2.2 Repository 목록

| Repository | 주요 메서드 | 특징 |
|-----------|----------|------|
| PageRepository | findBySlug, findByIsPublishedTrue | Slug 기반 조회 |
| PastorRepository | findByIsActiveTrueOrderByDisplayOrder | 표시 순서 정렬 |
| StaffRepository | findByRole, findByDepartment | 역할/부서별 조회 |
| WorshipRepository | findByDayOfWeek, findByIsLiveNowTrue | 요일/라이브 조회 |
| YouTubeLiveRepository | findByStatus, findByYoutubeVideoId | 상태별 조회 |
| SermonRepository | findByPreacher, incrementViewCount | 설교자별, 조회수 증가 |
| YouTubePlaylistRepository | findByCategory, findByLastSyncedAtBefore | 카테고리, 동기화 조회 |
| HymnRepository | findByArtist, incrementViewCount | 아티스트별, 조회수 증가 |
| MinistryRepository | findByCategory, findByTargetAge | 카테고리/연령별 |
| MissionRepository | findOngoingMissions, calculateTotalSupportAmount | 진행 중 선교, 후원금 집계 |
| NoticeRepository | findByCategory, searchByKeyword, incrementViewCount | 카테고리 검색, 조회수 |
| NoticeAttachmentRepository | findByNoticeId | 공지별 첨부파일 |
| BulletinRepository | findByBulletinDate, incrementDownloadCount | 날짜 조회, 다운로드수 |
| GalleryRepository | searchByKeyword, incrementViewCount | 검색, 조회수 |
| GalleryImageRepository | findByGalleryIdOrderByDisplayOrder | 표시 순서 정렬 |
| VideoGalleryRepository | findByCategory, incrementViewCount | 카테고리, 조회수 |
| TestimonyRepository | findByIsApproved, incrementViewCount | 승인 상태, 조회수 |
| PrayerRequestRepository | findByStatus, incrementPrayerCount | 상태별, 기도수 |
| DonationAccountRepository | findByDonationType | 헌금 유형별 |
| EventRepository | findOngoingEvents, incrementParticipants | 진행 중 행사, 참가자 수 |

---

## 3. Service Layer (10개)

### 3.1 설계 원칙

✅ **Transaction 관리**
- `@Transactional(readOnly = true)` - 조회 최적화
- `@Transactional` - 데이터 변경

✅ **비즈니스 로직**
- 조회수/다운로드수/기도수 자동 증가
- 승인 프로세스 (간증, 기도요청)
- 상태 자동 관리
- 중복 검증

✅ **예외 처리**
- `IllegalArgumentException` - 명확한 에러 메시지
- Optional 활용 - null 안전성

✅ **페이징 지원**
- 모든 목록 조회에 Pageable

### 3.2 Service 목록

#### PageService
- Slug 기반 페이지 조회
- 공개 페이지 목록
- CRUD + Slug 중복 검증

#### WorshipService
- 활성화된 예배 목록
- 요일/유형별 조회
- 라이브 상태 토글 (다른 라이브 자동 해제)

#### SermonService
- 공개 설교 목록 (페이징)
- 설교자/날짜/본문/태그 검색
- 조회 시 조회수 자동 증가
- 추천 설교, 최신 설교

#### NoticeService
- 카테고리별 공지사항
- 상단 고정 공지사항
- 조회 시 조회수 자동 증가
- 제목/내용 검색

#### BulletinService
- 날짜별 주보 조회
- 다운로드 시 다운로드수 자동 증가
- 날짜 중복 검증

#### GalleryService
- 갤러리 + 이미지 통합 관리
- 조회 시 조회수 자동 증가
- 갤러리 삭제 시 이미지 자동 삭제

#### YouTubeLiveService
- 라이브 상태별 조회 (UPCOMING, LIVE, COMPLETED)
- 상태 변경 시 시간 자동 업데이트
- 오래된 데이터 정리 (스케줄링용)

#### TestimonyService
- 승인/대기 간증 분리
- 승인 시 publishedAt 자동 설정
- 승인된 간증만 조회수 증가

#### PrayerRequestService
- 상태별 기도요청 (PENDING, PRAYING, ANSWERED)
- 승인 시 상태 자동 변경 (PENDING → PRAYING)
- 응답 시 answeredAt 자동 설정
- 기도수 증가

#### MinistryService
- 교육/양육 부서 관리
- 선교 사업 관리 (통합)
- 진행 중/종료 선교 조회
- 후원금액 집계

---

## 4. 기술 스펙

### 4.1 기술 스택

- **Framework**: Spring Boot 4.0.2
- **Database**: PostgreSQL 18.1
- **ORM**: Spring Data JPA (Jakarta EE)
- **Build**: Maven

### 4.2 주요 어노테이션

#### Entity
```java
@Entity
@Table(name = "table_name", indexes = {...})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"relationship_fields"})
```

#### Repository
```java
@Repository
public interface XxxRepository extends JpaRepository<Entity, Long> {
    // Query methods
    Page<Entity> findByCondition(Pageable pageable);

    @Query("JPQL query")
    List<Entity> customQuery(@Param("param") String param);

    @Modifying
    @Query("UPDATE query")
    void updateQuery(@Param("id") Long id);
}
```

#### Service
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class XxxService {
    private final XxxRepository repository;

    @Transactional
    public Entity create(Entity entity) { ... }
}
```

### 4.3 JPA Auditing

BaseEntity를 통한 자동 감사:
- `@CreatedDate` - createdAt
- `@LastModifiedDate` - updatedAt
- `@EntityListeners(AuditingEntityListener.class)`

---

## 5. 구현 체크리스트

### 5.1 Entity Layer ✅
- [x] BaseEntity 생성 (JPA Auditing)
- [x] 20개 Entity 생성
- [x] 4개 Enum 생성 (WorshipType, NoticeCategory, LiveStatus, StaffRole, MinistryCategory, MissionType, PrayerStatus)
- [x] Context7 Best Practice 적용 (@EqualsAndHashCode, @ToString)
- [x] 적절한 Index 설정

### 5.2 Repository Layer ✅
- [x] 20개 Repository 생성
- [x] Spring Data JPA 네이밍 규칙 적용
- [x] Pageable 지원
- [x] 커스텀 @Query 작성
- [x] @Modifying 업데이트 쿼리
- [x] Context7 검증 완료 (5/5 stars)

### 5.3 Service Layer ✅
- [x] 10개 Service 생성
- [x] Transaction 관리 적용
- [x] 비즈니스 로직 구현
- [x] 조회수/다운로드수 자동 증가
- [x] 승인 프로세스
- [x] 중복 검증
- [x] 예외 처리

### 5.4 예정 작업 ⏳
- [ ] DTO Layer (17 Request + 17 Response)
- [ ] Controller Layer (10개 REST API)
- [ ] Exception Handler (@RestControllerAdvice)
- [ ] YouTube API 통합 (Client, Service, Scheduler)
- [ ] Database Migration (Flyway)

---

## 6. API 엔드포인트 설계 (예정)

### 6.1 RESTful 구조

```
/api/v1/pages
/api/v1/pastors
/api/v1/staff
/api/v1/worships
/api/v1/youtube-lives
/api/v1/sermons
/api/v1/hymns
/api/v1/ministries
/api/v1/missions
/api/v1/notices
/api/v1/bulletins
/api/v1/galleries
/api/v1/testimonies
/api/v1/prayer-requests
/api/v1/donations
/api/v1/events
```

### 6.2 공통 응답 포맷

```json
{
  "success": true,
  "data": { ... },
  "message": "Success message",
  "timestamp": "2026-02-03T10:00:00Z"
}
```

---

## 7. 품질 기준

### 7.1 코드 품질
- ✅ Context7 검증 통과 (Spring Data JPA Best Practices)
- ✅ Lombok 활용으로 보일러플레이트 최소화
- ✅ 명확한 네이밍 (한글 주석 + 영문 코드)

### 7.2 성능 최적화
- ✅ 적절한 Index 설정
- ✅ Lazy Loading (ManyToOne, OneToMany)
- ✅ @Transactional(readOnly = true) 조회 최적화
- ✅ @Modifying으로 효율적인 업데이트

### 7.3 보안
- ✅ SQL Injection 방지 (Named Parameters)
- ⏳ 입력 검증 (DTO Validation - 예정)
- ⏳ 인증/권한 (Spring Security - 예정)

---

## 8. 다음 단계

1. **DTO Layer 생성** - Request/Response DTO 34개
2. **Controller Layer 생성** - REST API 10개
3. **Exception Handler** - 통합 예외 처리
4. **YouTube API 통합** - 실시간 라이브, 자동 동기화
5. **Database Migration** - Flyway 스크립트
6. **통합 테스트** - API 테스트

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 |
|-----|------|----------|
| 2026-02-03 | 1.0 | 초기 설계 문서 생성 (구현 코드 기반 역방향) |
