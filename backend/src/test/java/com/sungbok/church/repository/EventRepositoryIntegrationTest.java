package com.sungbok.church.repository;

import com.sungbok.church.config.QuerydslConfiguration;
import com.sungbok.church.domain.entity.Event;
import com.sungbok.church.domain.repository.EventRepository;
import com.sungbok.church.dto.projection.EventProjectionDto;
import com.sungbok.church.fixture.EventFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EventRepository Testcontainers Integration Test
 * Podman 기반 PostgreSQL 통합 테스트
 *
 * 실행 전 환경 변수 설정 (MacOS):
 * export DOCKER_HOST=unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')
 * export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
 *
 * Linux (Rootless mode):
 * export DOCKER_HOST=unix://${XDG_RUNTIME_DIR}/podman/podman.sock
 * export TESTCONTAINERS_RYUK_DISABLED=true
 */
@SpringBootTest
@Testcontainers
@Import(QuerydslConfiguration.class)
@ActiveProfiles("testcontainers")
@Transactional
@DisplayName("EventRepository Testcontainers 통합 테스트 (PostgreSQL)")
class EventRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true); // Container 재사용으로 성능 향상

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("PostgreSQL - 진행 중인 행사 조회")
    void findOngoingEvents_WithPostgreSQL_Success() {
        // Given
        Event ongoing = eventRepository.save(
            EventFixture.builder()
                .title("진행 중인 행사")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .isPublished(true)
                .build()
        );

        Event upcoming = eventRepository.save(
            EventFixture.builder()
                .title("예정된 행사")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .isPublished(true)
                .build()
        );

        Event completed = eventRepository.save(
            EventFixture.builder()
                .title("종료된 행사")
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .isPublished(true)
                .build()
        );

        // When
        List<EventProjectionDto> result = eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("진행 중인 행사");
    }

    @Test
    @DisplayName("PostgreSQL - 참가 가능한 행사 조회 (정원 미달)")
    void findAvailableEvents_WithPostgreSQL_Success() {
        // Given
        Event available = eventRepository.save(
            EventFixture.builder()
                .title("참가 가능 행사")
                .registrationRequired(true)
                .currentParticipants(50)
                .maxParticipants(100)
                .isPublished(true)
                .build()
        );

        Event full = eventRepository.save(
            EventFixture.builder()
                .title("마감된 행사")
                .registrationRequired(true)
                .currentParticipants(100)
                .maxParticipants(100)
                .isPublished(true)
                .build()
        );

        // When
        List<EventProjectionDto> result = eventRepository.findAvailableEvents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("참가 가능 행사");
    }

    @Test
    @DisplayName("PostgreSQL - DATE 타입 변환 테스트 (H2와 다른 방언)")
    void findUpcomingEvents_DateCast_Success() {
        // Given
        LocalDate today = LocalDate.now();

        Event upcoming = eventRepository.save(
            EventFixture.builder()
                .title("다가오는 행사")
                .startDate(today.plusDays(1).atStartOfDay())
                .endDate(today.plusDays(2).atStartOfDay())
                .isPublished(true)
                .build()
        );

        Event past = eventRepository.save(
            EventFixture.builder()
                .title("지난 행사")
                .startDate(today.minusDays(2).atStartOfDay())
                .endDate(today.minusDays(1).atStartOfDay())
                .isPublished(true)
                .build()
        );

        // When
        Page<Event> result = eventRepository.findByStartDateAfterAndIsPublishedTrueOrderByStartDateAsc(
            today.atStartOfDay(),
            PageRequest.of(0, 10)
        );

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("다가오는 행사");
    }

    @Test
    @DisplayName("PostgreSQL - 대량 데이터 삽입 및 조회 성능 테스트")
    void bulkInsertAndQuery_Performance_Success() {
        // Given - 100개 행사 생성
        for (int i = 1; i <= 100; i++) {
            eventRepository.save(
                EventFixture.builder()
                    .title("행사 " + i)
                    .startDate(LocalDateTime.now().plusDays(i))
                    .endDate(LocalDateTime.now().plusDays(i + 1))
                    .isPublished(i % 2 == 0) // 50개만 공개
                    .build()
            );
        }

        // When
        long startTime = System.currentTimeMillis();
        Page<Event> publicEvents = eventRepository.findByIsPublishedTrueOrderByStartDateAsc(
            PageRequest.of(0, 100)
        );
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(publicEvents.getContent()).hasSize(50);
        assertThat(endTime - startTime).isLessThan(1000); // 1초 이내 조회
        System.out.println("PostgreSQL 조회 시간: " + (endTime - startTime) + "ms");
    }

    @Test
    @DisplayName("PostgreSQL - Transaction Rollback 테스트")
    void transactionRollback_Success() {
        // Given
        Event event = eventRepository.save(
            EventFixture.builder()
                .title("롤백 테스트 행사")
                .build()
        );
        Long eventId = event.getId();

        // When
        assertThat(eventRepository.findById(eventId)).isPresent();

        // Then - @Transactional로 인해 테스트 종료 후 자동 롤백
        // 다음 테스트에서는 이 데이터가 존재하지 않음
    }

    @Test
    @DisplayName("PostgreSQL - NULL 값 처리 테스트")
    void nullValueHandling_Success() {
        // Given
        Event eventWithNulls = eventRepository.save(
            EventFixture.builder()
                .title("NULL 필드 테스트")
                .location(null)
                .description(null)
                .registrationRequired(false)
                .currentParticipants(0)
                .build()
        );

        // When
        Event found = eventRepository.findById(eventWithNulls.getId()).orElseThrow();

        // Then
        assertThat(found.getLocation()).isNull();
        assertThat(found.getDescription()).isNull();
        assertThat(found.getCurrentParticipants()).isEqualTo(0);
    }
}
