package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Ministry;
import com.sungbok.church.domain.enums.MinistryCategory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Ministry 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryResponse {

    private Long id;
    private String name;
    private MinistryCategory category;
    private String description;
    private String targetAge;
    private String schedule;
    private String location;
    private String leader;
    private String contact;
    private String photoUrl;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static MinistryResponse from(Ministry ministry) {
        return MinistryResponse.builder()
            .id(ministry.getId())
            .name(ministry.getName())
            .category(ministry.getCategory())
            .description(ministry.getDescription())
            .targetAge(ministry.getTargetAge())
            .schedule(ministry.getSchedule())
            .location(ministry.getLocation())
            .leader(ministry.getLeader())
            .contact(ministry.getContact())
            .photoUrl(ministry.getPhotoUrl())
            .isActive(ministry.getIsActive())
            .displayOrder(ministry.getDisplayOrder())
            .createdAt(ministry.getCreatedAt())
            .updatedAt(ministry.getUpdatedAt())
            .build();
    }
}
