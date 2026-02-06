package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Event;
import com.sungbok.church.dto.request.EventRequest;
import com.sungbok.church.dto.response.EventResponse;
import com.sungbok.church.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event Controller
 * 행사/이벤트 관리 REST API
 */
@Tag(name = "행사", description = "행사 관리 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 공개된 행사 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getPublishedEvents(
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<EventResponse> events = eventService.getPublishedEvents(pageable)
            .map(EventResponse::from);
        return ResponseEntity.ok(events);
    }

    /**
     * 진행 중인 행사 조회
     */
    @GetMapping("/ongoing")
    public ResponseEntity<List<EventResponse>> getOngoingEvents() {
        List<EventResponse> events = eventService.getOngoingEvents().stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    /**
     * 예정된 행사 조회
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> events = eventService.getUpcomingEvents().stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    /**
     * 카테고리별 행사 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<EventResponse>> getEventsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EventResponse> events = eventService.getEventsByCategory(category, pageable)
            .map(EventResponse::from);
        return ResponseEntity.ok(events);
    }

    /**
     * 날짜 범위로 행사 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<EventResponse>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EventResponse> events = eventService.getEventsByDateRange(startDate, endDate, pageable)
            .map(EventResponse::from);
        return ResponseEntity.ok(events);
    }

    /**
     * 행사 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(EventResponse.from(event));
    }

    /**
     * 등록 가능한 행사 조회
     */
    @GetMapping("/registrable")
    public ResponseEntity<List<EventResponse>> getRegistrableEvents() {
        List<EventResponse> events = eventService.getRegistrableEvents().stream()
            .map(EventResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }

    /**
     * 행사 생성
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = toEntity(request);
        Event created = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(created));
    }

    /**
     * 행사 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        Event event = toEntity(request);
        Event updated = eventService.updateEvent(id, event);
        return ResponseEntity.ok(EventResponse.from(updated));
    }

    /**
     * 행사 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 카테고리별 행사 개수
     */
    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Long> getEventCountByCategory(@PathVariable String category) {
        return ResponseEntity.ok(eventService.getEventCountByCategory(category));
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Event toEntity(EventRequest request) {
        return Event.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .category(request.getCategory())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .location(request.getLocation())
            .organizer(request.getOrganizer())
            .posterImageUrl(request.getPosterImageUrl())
            .registrationRequired(request.getRegistrationRequired())
            .maxParticipants(request.getMaxParticipants())
            .isPublished(request.getIsPublished())
            .build();
    }
}
