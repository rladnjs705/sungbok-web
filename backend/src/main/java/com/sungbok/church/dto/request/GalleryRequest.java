package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Gallery 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    private String description;

    @NotNull(message = "행사 날짜는 필수입니다")
    private LocalDate eventDate;

    @Size(max = 500, message = "커버 이미지 URL은 500자 이하여야 합니다")
    private String coverImageUrl;

    @NotNull(message = "공개 여부는 필수입니다")
    @Builder.Default
    private Boolean isPublished = true;
}
