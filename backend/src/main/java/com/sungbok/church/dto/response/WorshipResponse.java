package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Worship 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorshipResponse {

    private Long id;
    private WorshipType type;
    private String title;
    private String description;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private String location;
    private String liveStreamUrl;
    private Boolean isLiveNow;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static WorshipResponse from(Worship worship) {
        return WorshipResponse.builder()
            .id(worship.getId())
            .type(worship.getType())
            .title(worship.getTitle())
            .description(worship.getDescription())
            .dayOfWeek(worship.getDayOfWeek())
            .startTime(worship.getStartTime())
            .location(worship.getLocation())
            .liveStreamUrl(worship.getLiveStreamUrl())
            .isLiveNow(worship.getIsLiveNow())
            .isActive(worship.getIsActive())
            .createdAt(worship.getCreatedAt())
            .updatedAt(worship.getUpdatedAt())
            .build();
    }
}
