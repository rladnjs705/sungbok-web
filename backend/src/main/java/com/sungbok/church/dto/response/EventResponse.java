package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Event;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Event 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean registrationRequired;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 계산된 필드
    private Boolean isFull;
    private Boolean isOngoing;

    /**
     * Entity -> Response DTO 변환
     */
    public static EventResponse from(Event event) {
        LocalDateTime now = LocalDateTime.now();
        boolean isFull = event.getRegistrationRequired() &&
                        event.getCurrentParticipants() >= event.getMaxParticipants();
        boolean isOngoing = now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate());

        return EventResponse.builder()
            .id(event.getId())
            .title(event.getTitle())
            .description(event.getDescription())
            .location(event.getLocation())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .registrationRequired(event.getRegistrationRequired())
            .maxParticipants(event.getMaxParticipants())
            .currentParticipants(event.getCurrentParticipants())
            .isPublished(event.getIsPublished())
            .isFull(isFull)
            .isOngoing(isOngoing)
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .build();
    }
}
