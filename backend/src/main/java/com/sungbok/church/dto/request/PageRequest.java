package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Page 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {

    @NotBlank(message = "Slug는 필수입니다")
    @Size(max = 100, message = "Slug는 100자 이하여야 합니다")
    private String slug;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Size(max = 300, message = "메타 설명은 300자 이하여야 합니다")
    private String metaDescription;

    @NotNull(message = "공개 여부는 필수입니다")
    @Builder.Default
    private Boolean isPublished = true;

    private Integer displayOrder;
}
