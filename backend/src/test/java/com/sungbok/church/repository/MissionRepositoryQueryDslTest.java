package com.sungbok.church.repository;

import com.sungbok.church.config.QuerydslConfiguration;
import com.sungbok.church.domain.entity.Mission;
import com.sungbok.church.domain.repository.MissionRepository;
import com.sungbok.church.dto.projection.MissionProjectionDto;
import com.sungbok.church.fixture.MissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mission Repository QueryDSL Test
 * 선교 Repository QueryDSL 테스트
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@SpringBootTest
@Import(QuerydslConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("MissionRepository QueryDSL 테스트")
class MissionRepositoryQueryDslTest extends BaseRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        missionRepository.deleteAll();
        flushAndClear();
    }

    @Test
    @DisplayName("진행 중인 선교 조회 - 시작일 <= 현재 < 종료일")
    void findOngoingMissions_Success() {
        // Given
        LocalDate now = LocalDate.now();

        Mission ongoing1 = persistAndFlush(
            MissionFixture.builder()
                .title("진행중 선교 1")
                .startDate(now.minusDays(10))
                .endDate(now.plusDays(10))
                .isActive(true)
                .build()
        );

        Mission ongoing2 = persistAndFlush(
            MissionFixture.builder()
                .title("진행중 선교 2")
                .startDate(now.minusDays(5))
                .endDate(null) // 종료일 없음 (무기한)
                .isActive(true)
                .build()
        );

        Mission upcoming = persistAndFlush(
            MissionFixture.builder()
                .title("예정된 선교")
                .startDate(now.plusDays(5))
                .endDate(now.plusDays(15))
                .isActive(true)
                .build()
        );

        Mission completed = persistAndFlush(
            MissionFixture.builder()
                .title("완료된 선교")
                .startDate(now.minusDays(20))
                .endDate(now.minusDays(5))
                .isActive(true)
                .build()
        );

        Mission inactive = persistAndFlush(
            MissionFixture.builder()
                .title("비활성화 선교")
                .startDate(now.minusDays(10))
                .endDate(now.plusDays(10))
                .isActive(false)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findOngoingMissions(now, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
            .extracting(MissionProjectionDto::getTitle)
            .containsExactlyInAnyOrder("진행중 선교 1", "진행중 선교 2");
    }

    @Test
    @DisplayName("진행 중인 선교 조회 - 빈 결과")
    void findOngoingMissions_EmptyResult() {
        // Given
        LocalDate now = LocalDate.now();

        persistAndFlush(
            MissionFixture.builder()
                .startDate(now.plusDays(5))
                .endDate(now.plusDays(15))
                .isActive(true)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findOngoingMissions(now, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("완료된 선교 조회 - 종료일 < 현재")
    void findCompletedMissions_Success() {
        // Given
        LocalDate now = LocalDate.now();

        Mission completed1 = persistAndFlush(
            MissionFixture.builder()
                .title("완료된 선교 1")
                .startDate(now.minusDays(30))
                .endDate(now.minusDays(5))
                .isActive(true)
                .build()
        );

        Mission completed2 = persistAndFlush(
            MissionFixture.builder()
                .title("완료된 선교 2")
                .startDate(now.minusDays(20))
                .endDate(now.minusDays(1))
                .isActive(true)
                .build()
        );

        Mission ongoing = persistAndFlush(
            MissionFixture.builder()
                .title("진행중 선교")
                .startDate(now.minusDays(10))
                .endDate(now.plusDays(10))
                .isActive(true)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findCompletedMissions(now, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
            .extracting(MissionProjectionDto::getTitle)
            .containsExactlyInAnyOrder("완료된 선교 1", "완료된 선교 2");
    }

    @Test
    @DisplayName("완료된 선교 조회 - 종료일이 null인 경우 제외")
    void findCompletedMissions_ExcludeNullEndDate() {
        // Given
        LocalDate now = LocalDate.now();

        persistAndFlush(
            MissionFixture.builder()
                .startDate(now.minusDays(10))
                .endDate(null) // 종료일 없음
                .isActive(true)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findCompletedMissions(now, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("총 후원금 계산 - 정상")
    void calculateTotalSupportAmount_Success() {
        // Given
        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(1000000L)
                .isActive(true)
                .build()
        );

        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(2000000L)
                .isActive(true)
                .build()
        );

        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(500000L)
                .isActive(true)
                .build()
        );

        // When
        Long totalAmount = missionRepository.calculateTotalSupportAmount();

        // Then
        assertThat(totalAmount).isEqualTo(3500000L);
    }

    @Test
    @DisplayName("총 후원금 계산 - 비활성화 선교 제외")
    void calculateTotalSupportAmount_ExcludeInactive() {
        // Given
        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(1000000L)
                .isActive(true)
                .build()
        );

        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(2000000L)
                .isActive(false) // 비활성화
                .build()
        );

        // When
        Long totalAmount = missionRepository.calculateTotalSupportAmount();

        // Then
        assertThat(totalAmount).isEqualTo(1000000L);
    }

    @Test
    @DisplayName("총 후원금 계산 - null 값 제외")
    void calculateTotalSupportAmount_ExcludeNull() {
        // Given
        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(1000000L)
                .isActive(true)
                .build()
        );

        persistAndFlush(
            MissionFixture.builder()
                .supportAmount(null) // null 값
                .isActive(true)
                .build()
        );

        // When
        Long totalAmount = missionRepository.calculateTotalSupportAmount();

        // Then
        assertThat(totalAmount).isEqualTo(1000000L);
    }

    @Test
    @DisplayName("총 후원금 계산 - 데이터 없을 때 0 반환")
    void calculateTotalSupportAmount_EmptyResult() {
        // Given
        // 데이터 없음

        // When
        Long totalAmount = missionRepository.calculateTotalSupportAmount();

        // Then
        assertThat(totalAmount).isEqualTo(0L);
    }

    @Test
    @DisplayName("진행 중인 선교 조회 - 페이징")
    void findOngoingMissions_Paging() {
        // Given
        LocalDate now = LocalDate.now();

        for (int i = 1; i <= 15; i++) {
            persistAndFlush(
                MissionFixture.builder()
                    .title("진행중 선교 " + i)
                    .startDate(now.minusDays(10))
                    .endDate(now.plusDays(10))
                    .isActive(true)
                    .build()
            );
        }

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findOngoingMissions(now, pageable);

        // Then
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("완료된 선교 조회 - 종료일 기준 정렬")
    void findCompletedMissions_OrderByEndDate() {
        // Given
        LocalDate now = LocalDate.now();

        Mission old = persistAndFlush(
            MissionFixture.builder()
                .title("오래된 선교")
                .startDate(now.minusDays(30))
                .endDate(now.minusDays(20))
                .isActive(true)
                .build()
        );

        Mission recent = persistAndFlush(
            MissionFixture.builder()
                .title("최근 선교")
                .startDate(now.minusDays(10))
                .endDate(now.minusDays(1))
                .isActive(true)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findCompletedMissions(now, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("최근 선교");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("오래된 선교");
    }

    @Test
    @DisplayName("MissionProjectionDto helper 메서드 - isOngoing()")
    void missionProjectionDto_isOngoing() {
        // Given
        LocalDate now = LocalDate.now();

        Mission ongoing = persistAndFlush(
            MissionFixture.builder()
                .startDate(now.minusDays(10))
                .endDate(now.plusDays(10))
                .isActive(true)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findOngoingMissions(now, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        MissionProjectionDto dto = result.getContent().get(0);
        assertThat(dto.isOngoing(now)).isTrue();
        assertThat(dto.isCompleted(now)).isFalse();
    }

    @Test
    @DisplayName("MissionProjectionDto helper 메서드 - isCompleted()")
    void missionProjectionDto_isCompleted() {
        // Given
        LocalDate now = LocalDate.now();

        Mission completed = persistAndFlush(
            MissionFixture.builder()
                .startDate(now.minusDays(30))
                .endDate(now.minusDays(5))
                .isActive(true)
                .build()
        );

        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<MissionProjectionDto> result = missionRepository.findCompletedMissions(now, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        MissionProjectionDto dto = result.getContent().get(0);
        assertThat(dto.isCompleted(now)).isTrue();
        assertThat(dto.isOngoing(now)).isFalse();
    }
}
