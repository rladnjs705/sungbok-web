# Church Feature Design Document

> **Feature**: church (QueryDSL 마이그레이션 및 테스트 코드 개선)
>
> **Version**: 1.0
> **Author**: Backend Development Team
> **Date**: 2026-02-10
> **Related Plan**: [church.plan.md](../../01-plan/features/church.plan.md)

---

## 1. Overview

### 1.1 Purpose
17개 Repository에 대한 QueryDSL 마이그레이션을 수행하여 복잡한 @Query를 타입 안전한 QueryDSL로 전환하고, 테스트 인프라를 개선하여 코드 재사용성과 유지보수성을 확보한다.

### 1.2 Scope
- **In Scope**:
  - QueryDSL Custom Repository 패턴 적용 (17개 Repository)
  - Test Fixtures 클래스 생성 (8개)
  - BaseRepositoryTest 추상 클래스 생성
  - DTO Projection 클래스 생성
  - Repository 테스트 작성

- **Out of Scope**:
  - Service/Controller 레이어 리팩토링
  - API 스펙 변경
  - 데이터베이스 스키마 변경

### 1.3 Success Criteria
- ✅ 모든 @Query 메서드가 QueryDSL로 전환
- ✅ N+1 쿼리 0건
- ✅ 모든 테스트 통과 (167개 + 추가 테스트)
- ✅ 테스트 커버리지 > 80%

---

## 2. Architecture

### 2.1 Custom Repository Pattern

```
┌─────────────────────────────────────────┐
│  JpaRepository<Entity, Long>            │
│  (Spring Data JPA 기본 메서드)            │
└────────────┬────────────────────────────┘
             │ extends
             │
┌────────────▼────────────────────────────┐
│  EventRepository                         │
│  - Spring Data JPA methods               │
│  - Custom methods from EventRepositoryCustom│
└────────────┬────────────────────────────┘
             │ extends
             │
┌────────────▼────────────────────────────┐
│  EventRepositoryCustom (interface)       │
│  - findOngoingEvents()                   │
│  - findAvailableEvents()                 │
│  - findUpcomingEvents()                  │
│  - searchByKeyword()                     │
└────────────┬────────────────────────────┘
             │ implements
             │
┌────────────▼────────────────────────────┐
│  EventRepositoryCustomImpl               │
│  - JPAQueryFactory                       │
│  - QueryDSL 구현                         │
│  - Projections.constructor()             │
└──────────────────────────────────────────┘
```

### 2.2 DTO Projection Pattern

```java
// Entity -> DTO Projection (N+1 방지)
SELECT new EventProjectionDto(
    e.id,
    e.title,
    e.category,
    // ... all fields
)
FROM Event e
LEFT JOIN e.worship w  // Eager loading
```

### 2.3 Test Infrastructure

```
┌─────────────────────────────────────────┐
│  BaseRepositoryTest (abstract)           │
│  - @SpringBootTest + @Transactional      │
│  - EntityManager helpers                 │
│  - persistAndFlush()                     │
│  - flushAndClear()                       │
└────────────┬────────────────────────────┘
             │ extends
             │
┌────────────▼────────────────────────────┐
│  EventRepositoryQueryDslTest             │
│  - @Autowired EventRepository            │
│  - EventFixture.builder() 사용           │
│  - 13 test cases                         │
└──────────────────────────────────────────┘
```

---

## 3. Component Design

### 3.1 Phase 1: Test Infrastructure

#### 3.1.1 BaseRepositoryTest

**파일 경로**: `backend/src/test/java/com/sungbok/church/repository/BaseRepositoryTest.java`

**설계**:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Autowired
    protected EntityManager em;

    protected <T> T persistAndFlush(T entity) {
        em.persist(entity);
        em.flush();
        em.clear();
        return entity;
    }

    protected void flushAndClear() {
        em.flush();
        em.clear();
    }
}
```

**책임**:
- 모든 Repository 테스트의 공통 설정 제공
- 영속성 컨텍스트 제어 헬퍼 메서드 제공
- H2 in-memory database 자동 설정

#### 3.1.2 Fixture Classes

**패턴**: Builder Pattern + Factory Methods

**EventFixture 예시**:
```java
public class EventFixture {

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    // Factory methods for common scenarios
    public static Event ongoing() {
        return builder()
            .startDate(LocalDateTime.now().minusDays(1))
            .endDate(LocalDateTime.now().plusDays(1))
            .isPublished(true)
            .build();
    }

    public static Event upcoming() {
        return builder()
            .startDate(LocalDateTime.now().plusDays(1))
            .endDate(LocalDateTime.now().plusDays(8))
            .isPublished(true)
            .build();
    }

    public static Event ended() {
        return builder()
            .startDate(LocalDateTime.now().minusDays(8))
            .endDate(LocalDateTime.now().minusDays(1))
            .isPublished(true)
            .build();
    }

    public static class EventBuilder {
        private String title = "테스트 행사";
        private String description = "행사 설명";
        private String category = "일반";
        private LocalDateTime startDate = LocalDateTime.now();
        private LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        private String location = "본당";
        private String organizer = "교육부";
        private Boolean registrationRequired = false;
        private Integer maxParticipants = 100;
        private Integer currentParticipants = 0;
        private Boolean isPublished = true;

        // Builder methods...
        public EventBuilder title(String title) {
            this.title = title;
            return this;
        }

        public Event build() {
            return Event.builder()
                .title(title)
                .description(description)
                .category(category)
                .startDate(startDate)
                .endDate(endDate)
                .location(location)
                .organizer(organizer)
                .registrationRequired(registrationRequired)
                .maxParticipants(maxParticipants)
                .currentParticipants(currentParticipants)
                .isPublished(isPublished)
                .build();
        }
    }
}
```

**생성 대상** (8개):
1. EventFixture.java
2. HymnFixture.java
3. WorshipFixture.java
4. MissionFixture.java
5. NoticeFixture.java
6. TestimonyFixture.java
7. BulletinFixture.java
8. PrayerRequestFixture.java

---

### 3.2 Phase 2.1: Event Repository

#### 3.2.1 EventProjectionDto

**파일 경로**: `backend/src/main/java/com/sungbok/church/dto/projection/EventProjectionDto.java`

**필드 목록** (16개):
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventProjectionDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String organizer;
    private String posterImageUrl;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean registrationRequired;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer viewCount;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isAvailable() {
        if (!registrationRequired) return true;
        return currentParticipants < maxParticipants;
    }

    public boolean isFull() {
        if (!registrationRequired) return false;
        return currentParticipants >= maxParticipants;
    }

    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate);
    }

    public boolean isEnded() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public boolean isUpcoming() {
        return LocalDateTime.now().isBefore(startDate);
    }
}
```

#### 3.2.2 EventRepositoryCustom

**파일 경로**: `backend/src/main/java/com/sungbok/church/domain/repository/custom/EventRepositoryCustom.java`

**메서드 시그니처**:
```java
public interface EventRepositoryCustom {

    /**
     * 진행 중인 행사 조회
     * @param currentDate 기준 날짜
     * @return 진행 중인 행사 목록 (시작일 순)
     */
    List<EventProjectionDto> findOngoingEvents(LocalDateTime currentDate);

    /**
     * 참가 가능한 행사 조회 (정원 미달)
     * @return 참가 가능한 행사 목록
     */
    List<EventProjectionDto> findAvailableEvents();

    /**
     * 다가오는 행사 조회
     * @param currentDate 기준 날짜
     * @return 다가오는 행사 목록
     */
    List<EventProjectionDto> findUpcomingEvents(LocalDate currentDate);

    /**
     * 키워드 검색 (제목, 설명, 장소)
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색 결과 (페이징)
     */
    Page<EventProjectionDto> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 카테고리별 공개 행사 조회
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @return 행사 목록 (페이징)
     */
    Page<EventProjectionDto> findByCategoryAndPublished(String category, Pageable pageable);

    /**
     * 기간별 공개 행사 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @param pageable 페이징 정보
     * @return 행사 목록 (페이징)
     */
    Page<EventProjectionDto> findByDateRangeAndPublished(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
}
```

#### 3.2.3 EventRepositoryCustomImpl

**파일 경로**: `backend/src/main/java/com/sungbok/church/domain/repository/custom/EventRepositoryCustomImpl.java`

**구현 예시**:
```java
@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventProjectionDto> findOngoingEvents(LocalDateTime currentDate) {
        return queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.startDate.loe(currentDate),
                event.endDate.goe(currentDate)
            )
            .orderBy(event.startDate.asc())
            .fetch();
    }

    @Override
    public List<EventProjectionDto> findAvailableEvents() {
        return queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                // ... all fields
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.registrationRequired.eq(true),
                event.currentParticipants.lt(event.maxParticipants)
            )
            .orderBy(event.startDate.asc())
            .fetch();
    }

    @Override
    public List<EventProjectionDto> findUpcomingEvents(LocalDate currentDate) {
        return queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                // ... all fields
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                Expressions.dateTemplate(
                    LocalDate.class,
                    "CAST({0} AS date)",
                    event.startDate
                ).goe(currentDate)
            )
            .orderBy(event.startDate.asc())
            .fetch();
    }

    @Override
    public Page<EventProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        List<EventProjectionDto> content = queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                // ... all fields
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.title.containsIgnoreCase(keyword)
                    .or(event.description.containsIgnoreCase(keyword))
                    .or(event.location.containsIgnoreCase(keyword))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(event.startDate.desc())
            .fetch();

        Long total = queryFactory
            .select(event.count())
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.title.containsIgnoreCase(keyword)
                    .or(event.description.containsIgnoreCase(keyword))
                    .or(event.location.containsIgnoreCase(keyword))
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
```

#### 3.2.4 EventRepositoryQueryDslTest

**파일 경로**: `backend/src/test/java/com/sungbok/church/repository/EventRepositoryQueryDslTest.java`

**테스트 케이스** (13개):
1. `findOngoingEvents_Success()` - 진행 중인 행사 조회
2. `findOngoingEvents_EmptyResult()` - 빈 결과 처리
3. `findAvailableEvents_Success()` - 참가 가능한 행사
4. `findAvailableEvents_AllFull()` - 모두 마감
5. `findUpcomingEvents_Success()` - 다가오는 행사
6. `searchByKeyword_TitleMatch()` - 키워드 검색 (제목)
7. `searchByKeyword_CaseInsensitive()` - 대소문자 구분 없음
8. `searchByKeyword_MultipleFields()` - 다중 필드 검색
9. `findByCategoryAndPublished_Success()` - 카테고리별 조회
10. `findByDateRangeAndPublished_Success()` - 기간별 조회
11. `pagingWorks_Correctly()` - 페이징 동작 검증
12. `unpublishedEventsNotReturned()` - 미공개 행사 필터링
13. `dtoProjectionWorks_Correctly()` - DTO Projection 정상 동작

---

### 3.3 Phase 2.2: Hymn Repository

#### 3.3.1 HymnProjectionDto

**필드**:
- id, title, number, lyrics, artist, composer, genre
- performanceCount, isFeatured, isPublished
- createdAt, updatedAt

#### 3.3.2 HymnRepositoryCustom

**메서드** (3개):
```java
public interface HymnRepositoryCustom {
    Page<HymnProjectionDto> searchByKeyword(String keyword, Pageable pageable);
    Page<HymnProjectionDto> findByArtistWithStats(String artist, Pageable pageable);
    Long calculateTotalPerformanceCount();
}
```

#### 3.3.3 구현 우선순위

1. **Day 1**: HymnProjectionDto + HymnRepositoryCustom 인터페이스
2. **Day 2**: HymnRepositoryCustomImpl + 테스트 작성

---

### 3.4 Phase 2.3-2.4: Mission, Notice, Testimony, VideoGallery

각 Repository는 동일한 패턴으로 마이그레이션:

**공통 작업**:
1. ProjectionDto 생성 (필수 필드 + 관계 필드)
2. RepositoryCustom 인터페이스 생성 (2-3개 메서드)
3. RepositoryCustomImpl 구현 (QueryDSL)
4. 테스트 작성 (각 메서드당 2-3개 테스트)

**예상 소요 시간**: 각 Repository당 4시간

---

## 4. Data Model

### 4.1 Event Entity Relationships

```
Event
  ├─ organizer (String) - 주최자
  ├─ category (String) - 카테고리
  └─ (No JPA relationships)
```

### 4.2 Hymn Entity Relationships

```
Hymn
  ├─ artist (String) - 아티스트
  ├─ composer (String) - 작곡가
  └─ (No JPA relationships)
```

### 4.3 Sermon Entity Relationships (참고용)

```
Sermon
  └─ worship (ManyToOne) - 예배
      ├─ @EntityGraph(attributePaths = {"worship"})
      └─ LEFT JOIN w in QueryDSL
```

---

## 5. API Design

**Note**: API 스펙 변경 없음. 기존 Service/Controller는 그대로 유지하며, Repository 레이어만 변경.

### 5.1 Response DTO 매핑

**EventResponse.from(EventProjectionDto)**:
```java
public static EventResponse from(EventProjectionDto dto) {
    return EventResponse.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        // ... 필드 매핑
        .isFull(dto.isFull())
        .isOngoing(dto.isOngoing())
        .build();
}
```

---

## 6. Testing Strategy

### 6.1 Unit Tests (Repository Layer)

**목표**: 각 Repository Custom 메서드 검증

**패턴**:
```java
@DisplayName("EventRepository QueryDSL 테스트")
class EventRepositoryQueryDslTest extends BaseRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("진행 중인 행사 조회 - 날짜 범위 필터")
    void findOngoingEvents_Success() {
        // Given - Fixture 사용
        Event ongoing = persistAndFlush(EventFixture.ongoing());
        Event upcoming = persistAndFlush(EventFixture.upcoming());

        // When
        List<EventProjectionDto> result =
            eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(ongoing.getTitle());
    }
}
```

### 6.2 Integration Tests (Service Layer)

**목표**: Service 레이어 기존 테스트 모두 통과

**변경 사항**:
- Repository mock 반환 타입: Entity → ProjectionDto
- Exception 타입: IllegalArgumentException → ResourceNotFoundException

### 6.3 Test Coverage Goals

- Repository Custom 메서드: 100%
- Service 메서드: > 90%
- 전체 테스트 커버리지: > 80%

---

## 7. Performance Considerations

### 7.1 N+1 Query Prevention

**Before (JPQL)**:
```sql
-- Query 1: Event 조회
SELECT * FROM event WHERE ...;

-- Query 2-N: 각 Event마다 Worship 조회 (발생하지 않음, Event는 관계 없음)
```

**After (QueryDSL)**:
```sql
-- Single Query: 모든 필드 한 번에 조회
SELECT e.id, e.title, ... FROM event e WHERE ...;
```

### 7.2 Query Optimization

1. **Index 활용**: WHERE 절에 indexed 컬럼 사용
   - event.isPublished (idx_event_published)
   - event.startDate (idx_event_start_date)

2. **Projection 최적화**: 필요한 컬럼만 SELECT
   - DTO Projection으로 불필요한 필드 제외

3. **페이징 최적화**: offset/limit 사용
   - count 쿼리 별도 실행 (필요시에만)

---

## 8. Migration Plan

### 8.1 Rollout Strategy

**Phase별 점진적 마이그레이션**:
1. Phase 1 완료 → Phase 2.1 시작
2. Phase 2.1 테스트 통과 → Phase 2.2 시작
3. 각 Phase별 독립적 배포 가능

### 8.2 Rollback Plan

**@Query 메서드 유지 전략** (선택):
- 기존 @Query 메서드를 `@Deprecated` 처리
- Custom 메서드 오류 시 빠른 롤백 가능
- **실제 적용**: @Query 메서드 삭제 (사용자 요청)

### 8.3 Deployment

**배포 순서**:
1. Repository Custom 구현체 배포 (기능 추가)
2. Service 레이어 Custom 메서드 사용으로 전환
3. 기존 @Query 메서드 제거 (Phase 완료 후)

---

## 9. Dependencies

### 9.1 Required Libraries

```gradle
// QueryDSL (이미 적용됨)
implementation 'io.github.openfeign.querydsl:querydsl-jpa:7.1'
annotationProcessor 'io.github.openfeign.querydsl:querydsl-apt:7.1:jpa'

// H2 Database (테스트용 - 추가됨)
testRuntimeOnly 'com.h2database:h2'

// Testcontainers (Phase 4에서 추가 예정)
// testImplementation 'org.testcontainers:testcontainers:2.0.3'
// testImplementation 'org.testcontainers:postgresql:2.0.3'
```

### 9.2 Configuration

**QuerydslConfiguration.java**:
```java
@Configuration
public class QuerydslConfiguration {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
```

**application-test.yml**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

---

## 10. Risk Analysis

### 10.1 Technical Risks

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| QueryDSL Q-class 생성 오류 | High | Low | `./gradlew clean compileJava` 재실행 |
| H2/PostgreSQL 방언 차이 | Medium | Medium | Testcontainers 통합 테스트 추가 |
| N+1 쿼리 미발견 | High | Low | SQL 로그 + 쿼리 카운트 검증 |
| 기존 테스트 실패 | Medium | Low | 점진적 마이그레이션 + 롤백 계획 |

### 10.2 Schedule Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| 예상보다 복잡한 쿼리 | 일정 지연 | 우선순위 재조정, High Priority만 완료 |
| 테스트 작성 시간 부족 | 품질 저하 | Fixture 재사용, 타임박스 설정 |
| Plan/Design 문서 부재 | 커뮤니케이션 비용 | 소급 생성 (현재 진행) |

---

## 11. Appendix

### 11.1 File Structure

```
backend/
├── src/main/java/com/sungbok/church/
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Event.java
│   │   │   ├── Hymn.java
│   │   │   └── ...
│   │   └── repository/
│   │       ├── EventRepository.java
│   │       └── custom/
│   │           ├── EventRepositoryCustom.java
│   │           └── EventRepositoryCustomImpl.java
│   ├── dto/
│   │   ├── projection/
│   │   │   ├── EventProjectionDto.java
│   │   │   └── ...
│   │   └── response/
│   │       └── EventResponse.java
│   └── service/
│       └── EventService.java
└── src/test/java/com/sungbok/church/
    ├── fixture/
    │   ├── EventFixture.java
    │   └── ...
    └── repository/
        ├── BaseRepositoryTest.java
        └── EventRepositoryQueryDslTest.java
```

### 11.2 Naming Conventions

- **Custom Interface**: `{Entity}RepositoryCustom`
- **Custom Implementation**: `{Entity}RepositoryCustomImpl`
- **Projection DTO**: `{Entity}ProjectionDto`
- **Fixture Class**: `{Entity}Fixture`
- **Test Class**: `{Entity}RepositoryQueryDslTest`

### 11.3 Code Review Checklist

- [ ] QueryDSL 쿼리가 타입 안전한가?
- [ ] N+1 쿼리가 없는가?
- [ ] DTO Projection이 올바르게 동작하는가?
- [ ] 테스트 커버리지가 충분한가? (> 80%)
- [ ] 모든 테스트가 통과하는가?
- [ ] 코드 스타일이 일관적인가?
- [ ] 주석이 충분한가?

---

**작성일**: 2026-02-10
**작성자**: Backend Development Team
**Version**: 1.0
**Status**: ✅ Complete
