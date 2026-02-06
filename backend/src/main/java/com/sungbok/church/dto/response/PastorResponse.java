package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Pastor;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Pastor 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PastorResponse {

    private Long id;
    private String name;
    private String position;
    private String photo;
    private String education;
    private String career;
    private String message;
    private String email;
    private String phone;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static PastorResponse from(Pastor pastor) {
        return PastorResponse.builder()
            .id(pastor.getId())
            .name(pastor.getName())
            .position(pastor.getPosition())
            .photo(pastor.getPhoto())
            .education(pastor.getEducation())
            .career(pastor.getCareer())
            .message(pastor.getMessage())
            .email(pastor.getEmail())
            .phone(pastor.getPhone())
            .displayOrder(pastor.getDisplayOrder())
            .isActive(pastor.getIsActive())
            .createdAt(pastor.getCreatedAt())
            .updatedAt(pastor.getUpdatedAt())
            .build();
    }
}
