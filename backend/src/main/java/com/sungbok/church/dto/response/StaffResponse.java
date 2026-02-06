package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Staff;
import com.sungbok.church.domain.enums.StaffRole;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Staff 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private Long id;
    private String name;
    private StaffRole role;
    private String department;
    private String photo;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static StaffResponse from(Staff staff) {
        return StaffResponse.builder()
            .id(staff.getId())
            .name(staff.getName())
            .role(staff.getRole())
            .department(staff.getDepartment())
            .photo(staff.getPhoto())
            .displayOrder(staff.getDisplayOrder())
            .isActive(staff.getIsActive())
            .createdAt(staff.getCreatedAt())
            .updatedAt(staff.getUpdatedAt())
            .build();
    }
}
