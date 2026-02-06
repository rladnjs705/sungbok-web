package com.sungbok.church.dto.request;

import com.sungbok.church.domain.enums.NoticeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notice 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRequest {

    @NotNull(message = "카테고리는 필수입니다")
    private NoticeCategory category;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotBlank(message = "작성자는 필수입니다")
    @Size(max = 50, message = "작성자는 50자 이하여야 합니다")
    private String author;

    @NotNull(message = "상단 고정 여부는 필수입니다")
    @Builder.Default
    private Boolean isPinned = false;

    private LocalDateTime publishedAt;
}
