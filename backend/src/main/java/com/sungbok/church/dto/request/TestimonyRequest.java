package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Testimony 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestimonyRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "작성자는 필수입니다")
    @Size(max = 50, message = "작성자는 50자 이하여야 합니다")
    private String author;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    private String category;
}
