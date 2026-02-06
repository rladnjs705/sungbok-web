package com.sungbok.church.dto.request;

import com.sungbok.church.domain.enums.MinistryCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Ministry 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryRequest {

    @NotBlank(message = "부서명은 필수입니다")
    @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
    private String name;

    @NotNull(message = "카테고리는 필수입니다")
    private MinistryCategory category;

    private String description;

    @Size(max = 50, message = "대상 연령은 50자 이하여야 합니다")
    private String targetAge;

    @Size(max = 200, message = "일정은 200자 이하여야 합니다")
    private String schedule;

    @Size(max = 100, message = "장소는 100자 이하여야 합니다")
    private String location;

    @Size(max = 50, message = "리더는 50자 이하여야 합니다")
    private String leader;

    @Size(max = 100, message = "연락처는 100자 이하여야 합니다")
    private String contact;

    @Size(max = 500, message = "사진 URL은 500자 이하여야 합니다")
    private String photoUrl;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Builder.Default
    private Boolean isActive = true;

    private Integer displayOrder;
}
