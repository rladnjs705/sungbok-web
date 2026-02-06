package com.sungbok.church.dto.request;

import com.sungbok.church.domain.enums.WorshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Worship 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorshipRequest {

    @NotNull(message = "예배 유형은 필수입니다")
    private WorshipType type;

    @NotBlank(message = "예배명은 필수입니다")
    @Size(max = 100, message = "예배명은 100자 이하여야 합니다")
    private String title;

    private String description;

    @NotNull(message = "요일은 필수입니다")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "시작 시간은 필수입니다")
    private LocalTime startTime;

    @Size(max = 100, message = "장소는 100자 이하여야 합니다")
    private String location;

    @Size(max = 500, message = "라이브 스트림 URL은 500자 이하여야 합니다")
    private String liveStreamUrl;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Builder.Default
    private Boolean isActive = true;
}
