package com.sungbok.church.dto.request;

import com.sungbok.church.domain.enums.MissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Mission 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotNull(message = "선교 유형은 필수입니다")
    private MissionType type;

    @Size(max = 100, message = "국가는 100자 이하여야 합니다")
    private String country;

    @Size(max = 100, message = "지역은 100자 이하여야 합니다")
    private String region;

    private String description;

    @Size(max = 50, message = "선교사명은 50자 이하여야 합니다")
    private String missionaryName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long supportAmount;

    @Size(max = 500, message = "사진 URL은 500자 이하여야 합니다")
    private String photoUrl;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Builder.Default
    private Boolean isActive = true;
}
