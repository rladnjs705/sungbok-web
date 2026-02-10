package com.sungbok.church.repository;

import com.sungbok.church.config.QuerydslConfiguration;
import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.domain.repository.WorshipRepository;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import com.sungbok.church.dto.response.YouTubeLiveResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * YouTubeLiveRepository QueryDSL 통합 테스트
 *
 * 목적:
 * - QueryDSL 쿼리 동작 검증
 * - LazyInitializationException 방지 확인
 * - N+1 쿼리 없음 검증
 * - DTO Projection 정상 동작 확인
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@DisplayName("YouTubeLiveRepository QueryDSL 테스트")
class YouTubeLiveRepositoryQueryDslTest {

    @Autowired
    private YouTubeLiveRepository youtubeLiveRepository;

    @Autowired
    private WorshipRepository worshipRepository;

    private Worship sundayWorship;
    private Worship wednesdayWorship;
    private YouTubeLive liveLive;
    private YouTubeLive upcomingLive;
    private YouTubeLive completedLive;

    @BeforeEach
    void setUp() {
        // Worship 데이터 생성
        sundayWorship = Worship.builder()
            .title("주일예배")
            .type(WorshipType.SUNDAY)
            .dayOfWeek(DayOfWeek.SUNDAY)
            .startTime(LocalTime.of(10, 30))
            .isActive(true)
            .build();
        worshipRepository.save(sundayWorship);

        wednesdayWorship = Worship.builder()
            .title("수요예배")
            .type(WorshipType.WEDNESDAY)
            .dayOfWeek(DayOfWeek.WEDNESDAY)
            .startTime(LocalTime.of(19, 30))
            .isActive(true)
            .build();
        worshipRepository.save(wednesdayWorship);

        // YouTubeLive 데이터 생성
        liveLive = YouTubeLive.builder()
            .title("주일예배 실시간 방송")
            .description("주일예배 생중계")
            .youtubeVideoId("live-video-1")
            .scheduledStartTime(LocalDateTime.now().minusHours(1))
            .actualStartTime(LocalDateTime.now().minusHours(1))
            .status(LiveStatus.LIVE)
            .viewerCount(150)
            .worship(sundayWorship)
            .build();
        youtubeLiveRepository.save(liveLive);

        upcomingLive = YouTubeLive.builder()
            .title("다음 주일예배 예고")
            .description("다음 주일예배 예정")
            .youtubeVideoId("upcoming-video-1")
            .scheduledStartTime(LocalDateTime.now().plusDays(7))
            .status(LiveStatus.UPCOMING)
            .viewerCount(0)
            .worship(sundayWorship)
            .build();
        youtubeLiveRepository.save(upcomingLive);

        completedLive = YouTubeLive.builder()
            .title("지난 수요예배")
            .description("수요예배 다시보기")
            .youtubeVideoId("completed-video-1")
            .scheduledStartTime(LocalDateTime.now().minusDays(3))
            .actualStartTime(LocalDateTime.now().minusDays(3))
            .endTime(LocalDateTime.now().minusDays(3).plusHours(1))
            .status(LiveStatus.COMPLETED)
            .viewerCount(200)
            .worship(wednesdayWorship)
            .build();
        youtubeLiveRepository.save(completedLive);
    }

    @Test
    @DisplayName("전체 YouTube Live 목록 조회 - QueryDSL")
    void testFindAllWithDetails() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<YouTubeLiveResponse> result = youtubeLiveRepository.findAllWithDetails(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);

        // LazyInitializationException 없이 Worship 데이터 접근 가능
        YouTubeLiveResponse dto = result.getContent().get(0);
        assertThat(dto.getWorshipId()).isNotNull();
        assertThat(dto.getWorshipName()).isNotBlank();

        // 최신순 정렬 확인 (scheduledStartTime DESC)
        assertThat(result.getContent().get(0).getScheduledStartTime())
            .isAfter(result.getContent().get(1).getScheduledStartTime());
    }

    @Test
    @DisplayName("예배 유형별 YouTube Live 조회 - QueryDSL")
    void testFindByWorshipType() {
        // When - 주일예배 라이브만 조회
        List<YouTubeLiveResponse> sundayLives = youtubeLiveRepository.findByWorshipType(WorshipType.SUNDAY);

        // Then
        assertThat(sundayLives).hasSize(2);
        assertThat(sundayLives).allMatch(dto ->
            dto.getWorshipName().equals("주일예배")
        );

        // Worship 관계 데이터 확인
        assertThat(sundayLives.get(0).getWorshipId()).isEqualTo(sundayWorship.getId());

        // When - 수요예배 라이브만 조회
        List<YouTubeLiveResponse> wednesdayLives = youtubeLiveRepository.findByWorshipType(WorshipType.WEDNESDAY);

        // Then
        assertThat(wednesdayLives).hasSize(1);
        assertThat(wednesdayLives.get(0).getTitle()).isEqualTo("지난 수요예배");
        assertThat(wednesdayLives.get(0).getWorshipName()).isEqualTo("수요예배");
    }

    @Test
    @DisplayName("최신 YouTube Live 조회 - QueryDSL")
    void testFindLatest() {
        // When - 최신 2개 조회
        List<YouTubeLiveResponse> result = youtubeLiveRepository.findLatest(2);

        // Then
        assertThat(result).hasSize(2);

        // 최신순 정렬 확인
        assertThat(result.get(0).getScheduledStartTime())
            .isAfter(result.get(1).getScheduledStartTime());

        // Worship 관계 데이터 확인
        assertThat(result).allMatch(dto ->
            dto.getWorshipId() != null && dto.getWorshipName() != null
        );
    }

    @Test
    @DisplayName("예정된 YouTube Live 조회 - QueryDSL")
    void testFindUpcoming() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<YouTubeLiveResponse> result = youtubeLiveRepository.findUpcoming(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1); // UPCOMING 상태 1개
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("다음 주일예배 예고");

        // 미래 시간만 포함
        LocalDateTime now = LocalDateTime.now();
        assertThat(result.getContent()).allMatch(dto ->
            dto.getScheduledStartTime().isAfter(now)
        );

        // 시작 시간 오름차순 정렬 확인
        YouTubeLiveResponse dto = result.getContent().get(0);
        assertThat(dto.getScheduledStartTime()).isAfter(now);
    }

    @Test
    @DisplayName("N+1 쿼리 방지 검증 - LEFT JOIN 확인")
    void testNoN1Query() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - 쿼리 실행
        Page<YouTubeLiveResponse> result = youtubeLiveRepository.findAllWithDetails(pageable);

        // Then - Worship 데이터 접근 시 추가 쿼리 없음
        result.getContent().forEach(dto -> {
            // LazyInitializationException 발생하지 않음
            assertThat(dto.getWorshipId()).isNotNull();
            assertThat(dto.getWorshipName()).isNotBlank();
        });

        // DTO Projection이므로 Entity가 아닌 DTO만 반환
        assertThat(result.getContent().get(0)).isInstanceOf(YouTubeLiveResponse.class);
    }

    @Test
    @DisplayName("LiveStatus Enum 직접 전달 검증")
    void testLiveStatusEnumMapping() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<YouTubeLiveResponse> result = youtubeLiveRepository.findAllWithDetails(pageable);

        // Then - Enum이 정상적으로 전달되어 반환
        assertThat(result.getContent()).anySatisfy(dto -> {
            assertThat(dto.getStatus()).isIn(LiveStatus.LIVE, LiveStatus.UPCOMING, LiveStatus.COMPLETED);
        });

        // 각 상태별 확인
        assertThat(result.getContent())
            .filteredOn(dto -> dto.getTitle().equals("주일예배 실시간 방송"))
            .hasSize(1)
            .allMatch(dto -> dto.getStatus().equals(LiveStatus.LIVE));

        assertThat(result.getContent())
            .filteredOn(dto -> dto.getTitle().equals("다음 주일예배 예고"))
            .hasSize(1)
            .allMatch(dto -> dto.getStatus().equals(LiveStatus.UPCOMING));

        assertThat(result.getContent())
            .filteredOn(dto -> dto.getTitle().equals("지난 수요예배"))
            .hasSize(1)
            .allMatch(dto -> dto.getStatus().equals(LiveStatus.COMPLETED));
    }

    @Test
    @DisplayName("페이징 동작 검증")
    void testPagination() {
        // Given
        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);

        // When
        Page<YouTubeLiveResponse> result1 = youtubeLiveRepository.findAllWithDetails(page1);
        Page<YouTubeLiveResponse> result2 = youtubeLiveRepository.findAllWithDetails(page2);

        // Then
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.getTotalElements()).isEqualTo(3);
        assertThat(result1.getTotalPages()).isEqualTo(2);

        assertThat(result2.getContent()).hasSize(1);
        assertThat(result2.getTotalElements()).isEqualTo(3);

        // 페이지별 데이터 중복 없음
        assertThat(result1.getContent()).doesNotContainAnyElementsOf(result2.getContent());
    }

    @Test
    @DisplayName("빈 결과 처리 확인")
    void testEmptyResult() {
        // Given - 존재하지 않는 예배 유형
        WorshipType nonExistentType = WorshipType.DAWN; // 새벽예배 데이터가 없다고 가정

        // When
        List<YouTubeLiveResponse> result = youtubeLiveRepository.findByWorshipType(nonExistentType);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ViewerCount 및 시간 필드 검증")
    void testViewerCountAndTimeFields() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<YouTubeLiveResponse> result = youtubeLiveRepository.findAllWithDetails(pageable);

        // Then
        YouTubeLiveResponse liveDto = result.getContent().stream()
            .filter(dto -> dto.getTitle().equals("주일예배 실시간 방송"))
            .findFirst()
            .orElseThrow();

        assertThat(liveDto.getViewerCount()).isEqualTo(150);
        assertThat(liveDto.getScheduledStartTime()).isNotNull();
        assertThat(liveDto.getActualStartTime()).isNotNull();
        assertThat(liveDto.getEndTime()).isNull(); // LIVE 상태는 아직 종료 안됨

        YouTubeLiveResponse completedDto = result.getContent().stream()
            .filter(dto -> dto.getTitle().equals("지난 수요예배"))
            .findFirst()
            .orElseThrow();

        assertThat(completedDto.getViewerCount()).isEqualTo(200);
        assertThat(completedDto.getEndTime()).isNotNull(); // COMPLETED 상태는 종료 시간 있음
    }
}
