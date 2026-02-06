package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * GalleryImage 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImageRequest {

    @NotNull(message = "갤러리 ID는 필수입니다")
    private Long galleryId;

    @NotBlank(message = "이미지 URL은 필수입니다")
    @Size(max = 500, message = "이미지 URL은 500자 이하여야 합니다")
    private String imageUrl;

    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    private String thumbnailUrl;

    @Size(max = 500, message = "캡션은 500자 이하여야 합니다")
    private String caption;

    @NotNull(message = "파일 크기는 필수입니다")
    private Long fileSize;

    private Integer width;

    private Integer height;

    private Integer displayOrder;
}
