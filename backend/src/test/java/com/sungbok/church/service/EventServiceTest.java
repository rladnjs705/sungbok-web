package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Event;
import com.sungbok.church.domain.repository.EventRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * EventService Unit Test
 *
 * Testing Strategy:
 * 1. Repository 메서드 호출 검증
 * 2. 비즈니스 로직 검증 (날짜 범위 조회, 진행중/예정 행사)
 * 3. 예외 처리 검증
 * 4. 조회수 증가 로직 검증 (Race Condition 방지)
 * 5. 삭제 전 존재 확인 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService 단위 테스트")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
                .title("2024년 부활절 행사")
                .description("부활절 예배 및 행사 안내")
                .category("절기예배")
                .startDate(LocalDateTime.of(2024, 3, 31, 10, 0))
                .endDate(LocalDateTime.of(2024, 3, 31, 12, 0))
                .location("본당")
                .organizer("교육부")
                .posterImageUrl("https://example.com/poster.jpg")
                .registrationRequired(true)
                .maxParticipants(200)
                .currentParticipants(0)
                .viewCount(0)
                .isPublished(true)
                .build();
    }

    @Test
    @DisplayName("공개된 행사 목록 조회")
    void getPublishedEvents_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> expectedPage = new PageImpl<>(List.of(testEvent));
        given(eventRepository.findByIsPublishedTrueOrderByStartDateDesc(pageable))
                .willReturn(expectedPage);

        // when
        Page<Event> result = eventService.getPublishedEvents(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("2024년 부활절 행사");
        verify(eventRepository, times(1)).findByIsPublishedTrueOrderByStartDateDesc(pageable);
    }

    @Test
    @DisplayName("진행 중인 행사 조회")
    void getOngoingEvents_Success() {
        // given
        given(eventRepository.findOngoingEvents(any(LocalDateTime.class)))
                .willReturn(List.of(testEvent));

        // when
        List<Event> result = eventService.getOngoingEvents();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(eventRepository, times(1)).findOngoingEvents(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("예정된 행사 조회")
    void getUpcomingEvents_Success() {
        // given
        given(eventRepository.findUpcomingEvents(any(LocalDate.class)))
                .willReturn(List.of(testEvent));

        // when
        List<Event> result = eventService.getUpcomingEvents();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(eventRepository, times(1)).findUpcomingEvents(any(LocalDate.class));
    }

    @Test
    @DisplayName("카테고리별 행사 조회")
    void getEventsByCategory_Success() {
        // given
        String category = "절기예배";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> expectedPage = new PageImpl<>(List.of(testEvent));
        given(eventRepository.findByCategoryAndIsPublishedTrue(category, pageable))
                .willReturn(expectedPage);

        // when
        Page<Event> result = eventService.getEventsByCategory(category, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(eventRepository, times(1)).findByCategoryAndIsPublishedTrue(category, pageable);
    }

    @Test
    @DisplayName("날짜 범위로 행사 조회")
    void getEventsByDateRange_Success() {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> expectedPage = new PageImpl<>(List.of(testEvent));
        given(eventRepository.findByStartDateBetweenAndIsPublishedTrue(startDate, endDate, pageable))
                .willReturn(expectedPage);

        // when
        Page<Event> result = eventService.getEventsByDateRange(startDate, endDate, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(eventRepository, times(1))
                .findByStartDateBetweenAndIsPublishedTrue(startDate, endDate, pageable);
    }

    @Test
    @DisplayName("행사 ID로 조회 성공 - 조회수 증가 및 EntityManager refresh 검증")
    void getEventById_Success_WithViewCountAndRefresh() {
        // given
        Long eventId = 1L;
        given(eventRepository.findById(eventId))
                .willReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).incrementViewCount(eventId);
        doNothing().when(entityManager).refresh(testEvent);

        // when
        Event result = eventService.getEventById(eventId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("2024년 부활절 행사");

        // Race Condition 방지를 위한 검증
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).incrementViewCount(eventId);
        verify(entityManager, times(1)).refresh(testEvent);
    }

    @Test
    @DisplayName("행사 ID로 조회 실패 - 존재하지 않는 ID")
    void getEventById_NotFound_ThrowsException() {
        // given
        Long eventId = 999L;
        given(eventRepository.findById(eventId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventService.getEventById(eventId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("행사를 찾을 수 없습니다");

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).incrementViewCount(anyLong());
        verify(entityManager, never()).refresh(any());
    }

    @Test
    @DisplayName("등록 가능한 행사 조회")
    void getRegistrableEvents_Success() {
        // given
        given(eventRepository.findByRegistrationRequiredTrueAndIsPublishedTrue())
                .willReturn(List.of(testEvent));

        // when
        List<Event> result = eventService.getRegistrableEvents();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRegistrationRequired()).isTrue();
        verify(eventRepository, times(1)).findByRegistrationRequiredTrueAndIsPublishedTrue();
    }

    @Test
    @DisplayName("행사 생성 성공")
    void createEvent_Success() {
        // given
        given(eventRepository.save(testEvent))
                .willReturn(testEvent);

        // when
        Event result = eventService.createEvent(testEvent);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("2024년 부활절 행사");
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    @DisplayName("행사 수정 성공")
    void updateEvent_Success() {
        // given
        Long eventId = 1L;
        Event updatedEvent = Event.builder()
                .title("수정된 부활절 행사")
                .description("수정된 설명")
                .category("절기예배")
                .startDate(LocalDateTime.of(2024, 4, 1, 10, 0))
                .endDate(LocalDateTime.of(2024, 4, 1, 12, 0))
                .location("수정된 장소")
                .organizer("교육부")
                .posterImageUrl("https://example.com/updated.jpg")
                .registrationRequired(false)
                .maxParticipants(300)
                .isPublished(true)
                .build();

        given(eventRepository.findById(eventId))
                .willReturn(Optional.of(testEvent));
        given(eventRepository.save(testEvent))
                .willReturn(testEvent);

        // when
        Event result = eventService.updateEvent(eventId, updatedEvent);

        // then
        assertThat(result).isNotNull();
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    @DisplayName("행사 수정 실패 - 존재하지 않는 ID")
    void updateEvent_NotFound_ThrowsException() {
        // given
        Long eventId = 999L;
        given(eventRepository.findById(eventId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventService.updateEvent(eventId, testEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("행사를 찾을 수 없습니다");

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("행사 삭제 성공 - 존재 확인 후 삭제")
    void deleteEvent_Success_WithExistenceCheck() {
        // given
        Long eventId = 1L;
        given(eventRepository.existsById(eventId))
                .willReturn(true);
        doNothing().when(eventRepository).deleteById(eventId);

        // when
        eventService.deleteEvent(eventId);

        // then
        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    @DisplayName("행사 삭제 실패 - 존재하지 않는 ID")
    void deleteEvent_NotFound_ThrowsException() {
        // given
        Long eventId = 999L;
        given(eventRepository.existsById(eventId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> eventService.deleteEvent(eventId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("행사를 찾을 수 없습니다");

        verify(eventRepository, times(1)).existsById(eventId);
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("통계: 카테고리별 행사 개수")
    void getEventCountByCategory_Success() {
        // given
        String category = "절기예배";
        given(eventRepository.countByCategoryAndIsPublished(category, true))
                .willReturn(5L);

        // when
        long result = eventService.getEventCountByCategory(category);

        // then
        assertThat(result).isEqualTo(5L);
        verify(eventRepository, times(1)).countByCategoryAndIsPublished(category, true);
    }
}
