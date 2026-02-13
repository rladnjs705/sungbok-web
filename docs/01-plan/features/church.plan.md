# QueryDSL 마이그레이션 및 테스트 코드 개선 계획

## 📋 프로젝트 개요

**Feature**: church (연장 작업)
**작업 범위**: 전체 Repository QueryDSL 마이그레이션 (17개) + 테스트 코드 품질 개선
**예상 기간**: 10일 (2주)
**우선순위**: High Priority (Event, Hymn, Mission 등) → Medium → Low

---

## 🎯 목표

### 1. QueryDSL 마이그레이션
- **대상**: 17개 미구현 Repository
- **방식**: Custom Repository 패턴 (일관성 + 확장성)
- **기준**: 복잡한 @Query가 많은 순서대로 우선순위 결정

### 2. 테스트 코드 개선
- **@SpringBootTest → @DataJpaTest**: 테스트 속도 향상
- **Test Fixtures 클래스**: 재사용 가능한 테스트 데이터 팩토리
- **BaseRepositoryTest**: 공통 설정 및 헬퍼 메서드
- **Testcontainers 추가** (선택): PostgreSQL 통합 테스트

---

## 📊 현황 분석

### 코드베이스 현황
- **전체 Repository**: 21개
- **QueryDSL Custom 구현 완료**: 4개 (Sermon, YouTubeLive, GalleryImage, Gallery)
- **미구현 Repository**: 17개
- **Spring Boot**: 4.0.2, **QueryDSL**: 7.1 (OpenFeign fork)
- **JUnit**: 5 (Jupiter), **H2**: testRuntimeOnly

### 기존 테스트 코드 분석

**✅ 잘된 점:**
1. AssertJ 플루언트 API 사용
2. @BeforeEach로 테스트 데이터 초기화
3. @DisplayName으로 명확한 테스트 명명
4. N+1 쿼리 방지 검증
5. DTO Projection 사용
6. 페이징, 빈 결과 처리 테스트

**⚠️ 개선 필요:**
1. @SpringBootTest 대신 @DataJpaTest 사용 (속도 개선)
2. TestEntityManager 활용 (영속성 컨텍스트 제어)
3. Test Fixtures 클래스 부재 (코드 중복)
4. BaseRepositoryTest 클래스 부재 (공통 설정 중복)

### Repository 우선순위 (@Query 개수 기준)

**High Priority (복잡한 쿼리 + 높은 사용빈도)**
1. **Event** (6 @Query) - 날짜 범위, 진행중/종료 상태, 참가자 관리
2. **Hymn** (3 @Query) - 다중 필드 검색, 집계
3. **Mission** (3 @Query) - 진행중/종료 상태, 집계 쿼리
4. **Notice** (2 @Query) - 검색, 카테고리 필터
5. **Testimony** (2 @Query) - 검색, 승인 상태 관리
6. **VideoGallery** (2 @Query) - 검색, 카테고리 필터

**Medium Priority (단순 쿼리 + 관계 조인)**
7. **Bulletin** (1 @Query)
8. **PrayerRequest** (1 @Query)
9-12. **YouTubePlaylist, Ministry, Staff, Pastor** (관계 조인 최적화)

**Low Priority (단순 CRUD)**
13-17. **Worship, User, NoticeAttachment, DonationAccount, Page**

---

## 🏗️ 구현 계획

### Phase 1: 인프라 구축 (Day 1-2)

#### 1.1 Test Fixtures 클래스 생성

**생성 경로**: `backend/src/test/java/com/sungbok/church/fixture/`

**생성할 Fixture 클래스** (8개):
- WorshipFixture.java
- EventFixture.java
- HymnFixture.java
- MissionFixture.java
- NoticeFixture.java
- TestimonyFixture.java
- BulletinFixture.java
- PrayerRequestFixture.java

**설계 원칙**:
```java
public class EventFixture {

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public static class EventBuilder {
        private String title = "테스트 행사";
        private LocalDateTime startDate = LocalDateTime.now();
        private LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        private boolean isPublished = true;
        private boolean registrationRequired = false;
        private int currentParticipants = 0;
        private int maxParticipants = 100;

        public EventBuilder title(String title) {
            this.title = title;
            return this;
        }

        // ... 기타 빌더 메서드

        public Event build() {
            return Event.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .isPublished(isPublished)
                .registrationRequired(registrationRequired)
                .currentParticipants(currentParticipants)
                .maxParticipants(maxParticipants)
                .build();
        }
    }
}
```

#### 1.2 BaseRepositoryTest 클래스 생성

**생성 경로**: `backend/src/test/java/com/sungbok/church/repository/BaseRepositoryTest.java`

**구현**:
```java
@DataJpaTest
@Import(QuerydslConfiguration.class)
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Autowired
    protected TestEntityManager em;

    /**
     * 엔티티를 영속화하고 플러시 + 캐시 초기화
     */
    protected <T> T persistAndFlush(T entity) {
        T persisted = em.persistAndFlush(entity);
        em.clear(); // 영속성 컨텍스트 초기화 (신선한 조회 보장)
        return persisted;
    }

    /**
     * 플러시 후 캐시 초기화
     */
    protected void flushAndClear() {
        em.flush();
        em.clear();
    }
}
```

#### 1.3 Testcontainers 설정 (선택적, Phase 4에서 추가)

**build.gradle 추가**:
```gradle
// Phase 4에서 추가 예정 (최신 버전 2.0.3)
testImplementation 'org.testcontainers:testcontainers:2.0.3'
testImplementation 'org.testcontainers:postgresql:2.0.3'
testImplementation 'org.testcontainers:junit-jupiter:2.0.3'
```

---

### Phase 2: High Priority Repository 마이그레이션 (Day 3-6)

#### 2.1 Event Repository (Day 3-4, 가장 복잡)

**마이그레이션 대상 메서드**:
- `findOngoingEvents()` - 날짜 범위 검색 (line 44-47)
- `findAvailableEvents()` - 참가자 수 < 정원 (line 89-93)
- `findUpcomingEvents()` - CAST(date) 사용 (line 122-125)

**생성 파일**:
1. `EventRepositoryCustom.java` (인터페이스)
2. `EventRepositoryCustomImpl.java` (구현체)
3. `EventProjectionDto.java` (DTO)
4. `EventRepositoryQueryDslTest.java` (테스트)

**Custom Interface**:
```java
public interface EventRepositoryCustom {
    List<EventProjectionDto> findOngoingEvents(LocalDateTime currentDate);
    List<EventProjectionDto> findAvailableEvents();
    Page<EventProjectionDto> findUpcomingEvents(LocalDate currentDate, Pageable pageable);
    Page<EventProjectionDto> searchByKeyword(String keyword, Pageable pageable);
}
```

**테스트 작성**:
```java
@DataJpaTest
@Import(QuerydslConfiguration.class)
class EventRepositoryQueryDslTest extends BaseRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("진행 중인 행사 조회 - 날짜 범위 필터")
    void findOngoingEvents_Success() {
        // Given - EventFixture 사용
        Event ongoing = persistAndFlush(
            EventFixture.builder()
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .build()
        );

        Event upcoming = persistAndFlush(
            EventFixture.builder()
                .startDate(LocalDateTime.now().plusDays(1))
                .build()
        );

        // When
        List<EventProjectionDto> result =
            eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(ongoing.getTitle());
    }

    @Test
    @DisplayName("참가 가능한 행사 조회 - 정원 미달")
    void findAvailableEvents_Success() {
        // Given
        Event available = persistAndFlush(
            EventFixture.builder()
                .registrationRequired(true)
                .currentParticipants(10)
                .maxParticipants(100)
                .build()
        );

        Event full = persistAndFlush(
            EventFixture.builder()
                .registrationRequired(true)
                .currentParticipants(100)
                .maxParticipants(100)
                .build()
        );

        // When
        List<EventProjectionDto> result = eventRepository.findAvailableEvents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(available.getId());
    }
}
```

#### 2.2 Hymn Repository (Day 5)

**마이그레이션 대상**:
- `searchByKeyword()` - 제목 + 가사 + 아티스트 검색
- `calculateTotalPerformanceCount()` - 집계

**Custom Interface**:
```java
public interface HymnRepositoryCustom {
    Page<HymnProjectionDto> searchByKeyword(String keyword, Pageable pageable);
    Page<HymnProjectionDto> findByArtistWithStats(String artist, Pageable pageable);
    Long calculateTotalPerformanceCount();
}
```

#### 2.3 Mission, Notice, Testimony, VideoGallery (Day 6)

**공통 패턴**:
- 제목/내용 검색
- 카테고리/상태 필터
- 페이징

각 Repository당 동일한 구조로 생성:
- Custom 인터페이스
- Custom 구현체
- Projection DTO
- 테스트 클래스

---

### Phase 3: Medium Priority Repository (Day 7-8)

#### 3.1 Bulletin, PrayerRequest
- 단순 @Query 1개씩
- Custom Repository 패턴 적용

#### 3.2 YouTubePlaylist, Ministry, Staff, Pastor
- 관계 조인 최적화 중심
- LEFT JOIN으로 N+1 방지
- DTO Projection

---

### Phase 4: 통합 테스트 강화 (Day 9, 선택적)

#### 4.1 Testcontainers 통합 테스트

**설정**:
```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test-containers")
class EventRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:18.1")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldWorkWithRealPostgreSQL() {
        // PostgreSQL 특화 기능 테스트
    }
}
```

---

### Phase 5: Low Priority 검토 (Day 10)

- Worship, User, NoticeAttachment, DonationAccount, Page
- Custom Repository 필요성 재검토
- 필요시 최소한의 최적화만 적용

---

## ✅ 검증 기준

### Phase별 검증 체크리스트

#### Phase 1 검증
- [ ] Fixture 클래스로 모든 Entity 생성 가능
- [ ] BaseRepositoryTest 상속으로 테스트 작성 간소화
- [ ] TestEntityManager로 영속성 컨텍스트 제어 가능

#### Phase 2-3 검증 (각 Repository별)
- [ ] 기존 @Query 메서드 → QueryDSL Custom 메서드 1:1 변환
- [ ] 모든 Custom 메서드에 테스트 코드 작성
- [ ] N+1 쿼리 발생하지 않음 (SQL 로그 확인)
- [ ] DTO Projection 정상 동작
- [ ] 페이징 테스트 통과
- [ ] 빈 결과 처리 테스트 통과
- [ ] 기존 Service/Controller 테스트 모두 통과

### 성능 검증

**쿼리 성능**:
- N+1 쿼리 0건
- LEFT JOIN 최대 2 depth
- 단일 쿼리 실행 시간 < 100ms

**테스트 성능**:
- Repository 테스트 (H2): < 5초
- 통합 테스트 (Testcontainers): < 30초
- 전체 테스트 스위트: < 2분

---

## 🔄 위험 요소 및 대응 방안

### 1. QueryDSL 7.1 호환성 이슈

**증상**: Q-class 생성 오류

**대응**:
- Q-class 재생성: `./gradlew clean compileJava`
- IDE Annotation Processor 활성화 확인
- 기존 @Query 메서드 `@Deprecated` 유지 (삭제 X)

### 2. N+1 쿼리 미발견

**대응**:
- `spring.jpa.properties.hibernate.session.events.log=true` 설정
- 테스트에서 쿼리 카운트 검증

### 3. H2 vs PostgreSQL 방언 차이

**대응**:
- Phase 4에서 Testcontainers 필수
- PostgreSQL 특화 함수는 QueryDSL Expressions.stringTemplate() 사용

### 4. 기존 코드 영향 (Breaking Changes)

**대응**:
- 기존 메서드 유지 (`@Deprecated`)
- Service 레이어 점진적 전환
- DTO 변경 최소화

### 5. 테스트 작성 시간 부족

**대응**:
- 우선순위 재조정 (High Priority만 완료)
- Pair Programming (복잡한 쿼리)
- 타임박스 설정 (각 Repository 최대 4시간)

---

## 📁 핵심 파일 목록

### 참조용 기존 파일
1. **SermonRepositoryCustomImpl.java** (`backend/src/main/java/com/sungbok/church/domain/repository/custom/`)
   - QueryDSL Custom Repository 패턴의 표준 구현
   - Projections.constructor() 사용법
   - LEFT JOIN 패턴

2. **SermonRepositoryQueryDslTest.java** (`backend/src/test/java/com/sungbok/church/repository/`)
   - Repository 테스트 패턴
   - BaseRepositoryTest로 리팩토링 예정

3. **EventRepository.java** (`backend/src/main/java/com/sungbok/church/domain/repository/`)
   - Phase 2.1의 첫 마이그레이션 대상
   - 6개 @Query 메서드 (가장 복잡)

4. **build.gradle** (`backend/`)
   - QueryDSL 7.1 설정
   - Testcontainers 의존성 추가 예정

5. **QuerydslConfiguration.java** (`backend/src/main/java/com/sungbok/church/config/`)
   - JPAQueryFactory Bean 설정
   - @DataJpaTest에서 @Import 필요

### 생성 예정 파일

**Phase 1** (Day 1-2):
- `backend/src/test/java/com/sungbok/church/fixture/*.java` (8개)
- `backend/src/test/java/com/sungbok/church/repository/BaseRepositoryTest.java`

**Phase 2** (Day 3-6):
- `backend/src/main/java/com/sungbok/church/domain/repository/custom/Event*.java` (3개)
- `backend/src/main/java/com/sungbok/church/dto/projection/EventProjectionDto.java`
- `backend/src/test/java/com/sungbok/church/repository/EventRepositoryQueryDslTest.java`
- Hymn, Mission, Notice, Testimony, VideoGallery에 대해 동일 패턴 반복

---

## 📊 예상 일정

| Phase | 작업 | 소요일 | 완료 기준 |
|-------|------|--------|----------|
| 1 | 인프라 구축 | 2일 | Fixture 8개 + BaseRepositoryTest |
| 2.1 | Event Repository | 2일 | Custom 구현 + 테스트 |
| 2.2-2.4 | Hymn/Mission/Notice/Testimony/VideoGallery | 2일 | 5개 Repository 마이그레이션 |
| 3 | Medium Priority | 2일 | 6개 Repository 최적화 |
| 4 | 통합 테스트 | 1일 | Testcontainers 설정 |
| 5 | Low Priority + 문서 | 1일 | 검토 + PDCA 문서 |

**Total**: 10일 (2주)

---

## 🎯 성공 기준

### 필수 (Must Have)
- [ ] High Priority 6개 Repository QueryDSL 마이그레이션 완료
- [ ] 모든 Custom 메서드에 테스트 코드 작성
- [ ] 기존 Service/Controller 테스트 100% 통과
- [ ] N+1 쿼리 0건

### 권장 (Should Have)
- [ ] Medium Priority 6개 Repository 마이그레이션
- [ ] @DataJpaTest 전환 완료
- [ ] BaseRepositoryTest 및 Fixture 클래스 활용

### 선택 (Nice to Have)
- [ ] Testcontainers 통합 테스트
- [ ] Low Priority Repository 검토
- [ ] 테스트 커버리지 > 80%

---

## 📚 참고 자료

### QueryDSL
- 기존 SermonRepositoryCustomImpl.java (프로젝트 내)
- [OpenFeign QueryDSL GitHub](https://github.com/OpenFeign/querydsl)

### 테스트
- 기존 SermonRepositoryQueryDslTest.java (프로젝트 내)
- [Spring Boot Testing 공식 문서](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)
- [Testcontainers 공식 문서](https://www.testcontainers.org/)

---

**작성일**: 2026-02-10
**작성자**: Claude Sonnet 4.5 (bkit v1.5.0)
**Feature**: church (연장 작업)
**PDCA Phase**: Plan
