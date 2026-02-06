package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Mission;
import com.sungbok.church.domain.enums.MissionType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mission 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionResponse {

    private Long id;
    private String title;
    private MissionType type;
    private String country;
    private String region;
    private String description;
    private String missionaryName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long supportAmount;
    private String photoUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 계산된 필드
    private Boolean isOngoing;

    /**
     * Entity -> Response DTO 변환
     */
    public static MissionResponse from(Mission mission) {
        LocalDate now = LocalDate.now();
        boolean isOngoing = mission.getStartDate() != null &&
                           mission.getStartDate().isBefore(now) &&
                           (mission.getEndDate() == null || mission.getEndDate().isAfter(now));

        return MissionResponse.builder()
            .id(mission.getId())
            .title(mission.getTitle())
            .type(mission.getType())
            .country(mission.getCountry())
            .region(mission.getRegion())
            .description(mission.getDescription())
            .missionaryName(mission.getMissionaryName())
            .startDate(mission.getStartDate())
            .endDate(mission.getEndDate())
            .supportAmount(mission.getSupportAmount())
            .photoUrl(mission.getPhotoUrl())
            .isActive(mission.getIsActive())
            .isOngoing(isOngoing)
            .createdAt(mission.getCreatedAt())
            .updatedAt(mission.getUpdatedAt())
            .build();
    }
}
