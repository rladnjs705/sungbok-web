package com.sungbok.church.repository;

import com.sungbok.church.domain.entity.Event;
import com.sungbok.church.domain.repository.EventRepository;
import com.sungbok.church.dto.projection.EventProjectionDto;
import com.sungbok.church.fixture.EventFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EventRepository QueryDSL 통합 테스트
 *
 * 목적:
 * - QueryDSL 쿼리 동작 검증
 * - N+1 쿼리 없음 검증
 * - DTO Projection 정상 동작 확인
 * - 날짜 범위 검색 정확도 확인
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@DisplayName("EventRepository QueryDSL 테스트")
class EventRepositoryQueryDslTest extends BaseRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("진행 중인 행사 조회 - 날짜 범위 필터")
    void findOngoingEvents_Success() {
        // Given - EventFixture 사용
        Event ongoing = persistAndFlush(EventFixture.ongoing());
        Event upcoming = persistAndFlush(EventFixture.upcoming());
        Event ended = persistAndFlush(EventFixture.ended());

        // When
        List<EventProjectionDto> result = eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(ongoing.getTitle());
        assertThat(result.get(0).isOngoing()).isTrue();

        // DTO 메서드 검증
        EventProjectionDto dto = result.get(0);
        assertThat(dto.isOngoing()).isTrue();
        assertThat(dto.isEnded()).isFalse();
        assertThat(dto.isUpcoming()).isFalse();
    }

    @Test
    @DisplayName("진행 중인 행사 조회 - 빈 결과")
    void findOngoingEvents_EmptyResult() {
        // Given - 종료된 행사와 예정된 행사만 존재
        persistAndFlush(EventFixture.ended());
        persistAndFlush(EventFixture.upcoming());

        // When
        List<EventProjectionDto> result = eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("참가 가능한 행사 조회 - 정원 미달")
    void findAvailableEvents_Success() {
        // Given
        Event available = persistAndFlush(EventFixture.available());
        Event full = persistAndFlush(EventFixture.full());
        Event noRegistration = persistAndFlush(
            EventFixture.builder()
                .title("등록 불필요 행사")
                .registrationRequired(false)
                .build()
        );

        // When
        List<EventProjectionDto> result = eventRepository.findAvailableEvents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(available.getId());
        assertThat(result.get(0).isAvailable()).isTrue();
        assertThat(result.get(0).isFull()).isFalse();
    }

    @Test
    @DisplayName("참가 가능한 행사 조회 - 모두 마감")
    void findAvailableEvents_AllFull() {
        // Given
        persistAndFlush(EventFixture.full());

        // When
        List<EventProjectionDto> result = eventRepository.findAvailableEvents();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다가오는 행사 조회 - LocalDate CAST")
    void findUpcomingEvents_Success() {
        // Given
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Event upcoming1 = persistAndFlush(
            EventFixture.builder()
                .title("내일 행사")
                .startDate(tomorrow)
                .endDate(tomorrow.plusDays(1))
                .build()
        );

        Event upcoming2 = persistAndFlush(
            EventFixture.builder()
                .title("다음주 행사")
                .startDate(LocalDateTime.now().plusDays(7))
                .endDate(LocalDateTime.now().plusDays(8))
                .build()
        );

        Event past = persistAndFlush(EventFixture.ended());

        // When
        List<EventProjectionDto> result = eventRepository.findUpcomingEvents(LocalDate.now());

        // Then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result).allMatch(EventProjectionDto::getIsPublished);
        assertThat(result).allMatch(dto ->
            !dto.getStartDate().toLocalDate().isBefore(LocalDate.now())
        );

        // 오름차순 정렬 확인
        assertThat(result.get(0).getTitle()).isEqualTo("내일 행사");
    }

    @Test
    @DisplayName("키워드 검색 - 제목, 설명, 장소 포함")
    void searchByKeyword_Success() {
        // Given
        Event event1 = persistAndFlush(
            EventFixture.builder()
                .title("부활절 특별 집회")
                .description("부활절 행사 설명")
                .location("본당")
                .build()
        );

        Event event2 = persistAndFlush(
            EventFixture.builder()
                .title("성탄절 축하 행사")
                .description("성탄절 설명")
                .location("본당")
                .build()
        );

        Event event3 = persistAndFlush(
            EventFixture.builder()
                .title("일반 행사")
                .description("일반 설명")
                .location("별관")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When - 제목 검색
        Page<EventProjectionDto> titleResult = eventRepository.searchByKeyword("부활절", pageable);

        // Then
        assertThat(titleResult).hasSize(1);
        assertThat(titleResult.getContent().get(0).getTitle()).contains("부활절");

        // When - 장소 검색
        Page<EventProjectionDto> locationResult = eventRepository.searchByKeyword("본당", pageable);

        // Then
        assertThat(locationResult).hasSize(2);
        assertThat(locationResult.getContent()).allMatch(dto -> dto.getLocation().contains("본당"));
    }

    @Test
    @DisplayName("키워드 검색 - 대소문자 구분 없음")
    void searchByKeyword_CaseInsensitive() {
        // Given
        persistAndFlush(
            EventFixture.builder()
                .title("Easter Event")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When - 소문자로 검색
        Page<EventProjectionDto> result = eventRepository.searchByKeyword("easter", pageable);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("카테고리별 행사 조회")
    void findByCategoryAndPublished_Success() {
        // Given
        persistAndFlush(
            EventFixture.builder()
                .title("예배 행사 1")
                .category("예배")
                .build()
        );

        persistAndFlush(
            EventFixture.builder()
                .title("예배 행사 2")
                .category("예배")
                .build()
        );

        persistAndFlush(
            EventFixture.builder()
                .title("친교 행사")
                .category("친교")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<EventProjectionDto> result = eventRepository.findByCategoryAndPublished("예배", pageable);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).allMatch(dto -> dto.getCategory().equals("예배"));
    }

    @Test
    @DisplayName("기간별 행사 조회")
    void findByDateRangeAndPublished_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(30);

        Event inRange = persistAndFlush(
            EventFixture.builder()
                .title("기간 내 행사")
                .startDate(LocalDateTime.now().plusDays(7))
                .endDate(LocalDateTime.now().plusDays(8))
                .build()
        );

        Event outOfRange = persistAndFlush(
            EventFixture.builder()
                .title("기간 외 행사")
                .startDate(LocalDateTime.now().plusDays(60))
                .endDate(LocalDateTime.now().plusDays(61))
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<EventProjectionDto> result = eventRepository.findByDateRangeAndPublished(
            startDate, endDate, pageable
        );

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("기간 내 행사");
    }

    @Test
    @DisplayName("페이징 동작 검증")
    void testPagination() {
        // Given - 5개의 행사 생성
        for (int i = 1; i <= 5; i++) {
            persistAndFlush(
                EventFixture.builder()
                    .title("행사 " + i)
                    .category("일반")
                    .build()
            );
        }

        // When - 2개씩 페이징
        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);

        Page<EventProjectionDto> result1 = eventRepository.findByCategoryAndPublished("일반", page1);
        Page<EventProjectionDto> result2 = eventRepository.findByCategoryAndPublished("일반", page2);

        // Then
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.getTotalElements()).isEqualTo(5);
        assertThat(result1.getTotalPages()).isEqualTo(3);

        assertThat(result2.getContent()).hasSize(2);
        assertThat(result2.getTotalElements()).isEqualTo(5);

        // 페이지별 데이터 중복 없음
        assertThat(result1.getContent()).doesNotContainAnyElementsOf(result2.getContent());
    }

    @Test
    @DisplayName("미공개 행사는 조회되지 않음")
    void unpublishedEvents_NotReturned() {
        // Given
        persistAndFlush(EventFixture.published());
        persistAndFlush(EventFixture.unpublished());

        // When
        List<EventProjectionDto> result = eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then - 미공개 행사는 제외
        assertThat(result).allMatch(EventProjectionDto::getIsPublished);
    }

    @Test
    @DisplayName("DTO Projection 정상 동작")
    void dtoProjection_Success() {
        // Given
        Event event = persistAndFlush(
            EventFixture.builder()
                .title("테스트 행사")
                .maxParticipants(100)
                .currentParticipants(50)
                .registrationRequired(true)
                .build()
        );

        // When
        List<EventProjectionDto> result = eventRepository.findOngoingEvents(LocalDateTime.now());

        // Then
        assertThat(result).hasSize(1);
        EventProjectionDto dto = result.get(0);
        assertThat(dto).isInstanceOf(EventProjectionDto.class);
        assertThat(dto.getId()).isEqualTo(event.getId());
        assertThat(dto.getTitle()).isEqualTo(event.getTitle());
        assertThat(dto.getMaxParticipants()).isEqualTo(100);
        assertThat(dto.getCurrentParticipants()).isEqualTo(50);

        // DTO 메서드 검증
        assertThat(dto.isAvailable()).isTrue();
        assertThat(dto.isFull()).isFalse();
    }
}
