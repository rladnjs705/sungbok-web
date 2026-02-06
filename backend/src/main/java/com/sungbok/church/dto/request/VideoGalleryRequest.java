package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * VideoGallery 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoGalleryRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    private String description;

    @Size(max = 50, message = "YouTube Video ID는 50자 이하여야 합니다")
    private String youtubeVideoId;

    @Size(max = 500, message = "비디오 URL은 500자 이하여야 합니다")
    private String videoUrl;

    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    private String thumbnailUrl;

    private LocalDate eventDate;

    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    private String category;

    @NotNull(message = "공개 여부는 필수입니다")
    @Builder.Default
    private Boolean isPublished = true;
}
