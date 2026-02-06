package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Bulletin 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletinRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotNull(message = "주보 날짜는 필수입니다")
    private LocalDate bulletinDate;

    @NotBlank(message = "PDF URL은 필수입니다")
    @Size(max = 500, message = "PDF URL은 500자 이하여야 합니다")
    private String pdfUrl;

    @NotNull(message = "파일 크기는 필수입니다")
    private Long fileSize;

    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    private String thumbnailUrl;

    @NotNull(message = "공개 여부는 필수입니다")
    @Builder.Default
    private Boolean isPublished = true;
}
