package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Event 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    private String description;

    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    private String category;

    @Size(max = 100, message = "주최자는 100자 이하여야 합니다")
    private String organizer;

    @Size(max = 500, message = "포스터 이미지 URL은 500자 이하여야 합니다")
    private String posterImageUrl;

    @Size(max = 100, message = "장소는 100자 이하여야 합니다")
    private String location;

    @NotNull(message = "시작 일시는 필수입니다")
    private LocalDateTime startDate;

    @NotNull(message = "종료 일시는 필수입니다")
    private LocalDateTime endDate;

    @NotNull(message = "등록 필요 여부는 필수입니다")
    @Builder.Default
    private Boolean registrationRequired = false;

    private Integer maxParticipants;

    @NotNull(message = "공개 여부는 필수입니다")
    @Builder.Default
    private Boolean isPublished = true;
}
