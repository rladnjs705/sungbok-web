package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Event;
import com.sungbok.church.domain.repository.EventRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Service
 * 행사/이벤트 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EntityManager entityManager;

    /**
     * 공개된 행사 목록 조회 (페이징)
     */
    public Page<Event> getPublishedEvents(Pageable pageable) {
        return eventRepository.findByIsPublishedTrueOrderByStartDateDesc(pageable);
    }

    /**
     * 진행 중인 행사 조회
     */
    public List<Event> getOngoingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findOngoingEvents(now);
    }

    /**
     * 예정된 행사 조회
     */
    public List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findUpcomingEvents(today);
    }

    /**
     * 카테고리별 행사 조회
     */
    public Page<Event> getEventsByCategory(String category, Pageable pageable) {
        return eventRepository.findByCategoryAndIsPublishedTrue(category, pageable);
    }

    /**
     * 날짜 범위로 행사 조회
     */
    public Page<Event> getEventsByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        return eventRepository.findByStartDateBetweenAndIsPublishedTrue(
            startDate, endDate, pageable
        );
    }

    /**
     * 행사 ID로 조회 및 조회수 증가
     */
    @Transactional
    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("행사를 찾을 수 없습니다: " + id));

        eventRepository.incrementViewCount(id);

        // 엔티티 새로고침 (업데이트된 조회수 반영)
        entityManager.refresh(event);

        return event;
    }

    /**
     * 등록 가능한 행사 조회
     */
    public List<Event> getRegistrableEvents() {
        return eventRepository.findByRegistrationRequiredTrueAndIsPublishedTrue();
    }

    /**
     * 행사 생성
     */
    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    /**
     * 행사 수정
     */
    @Transactional
    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("행사를 찾을 수 없습니다: " + id));

        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setCategory(updatedEvent.getCategory());
        event.setStartDate(updatedEvent.getStartDate());
        event.setEndDate(updatedEvent.getEndDate());
        event.setLocation(updatedEvent.getLocation());
        event.setOrganizer(updatedEvent.getOrganizer());
        event.setPosterImageUrl(updatedEvent.getPosterImageUrl());
        event.setRegistrationRequired(updatedEvent.getRegistrationRequired());
        event.setMaxParticipants(updatedEvent.getMaxParticipants());
        event.setIsPublished(updatedEvent.getIsPublished());

        return eventRepository.save(event);
    }

    /**
     * 행사 삭제
     */
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("행사를 찾을 수 없습니다: " + id);
        }
        eventRepository.deleteById(id);
    }

    /**
     * 통계: 카테고리별 행사 개수
     */
    public long getEventCountByCategory(String category) {
        return eventRepository.countByCategoryAndIsPublished(category, true);
    }
}
