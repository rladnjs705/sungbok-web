package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Sermon 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SermonRequest {

    private Long worshipId;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "설교자는 필수입니다")
    @Size(max = 50, message = "설교자는 50자 이하여야 합니다")
    private String preacher;

    @NotNull(message = "설교 날짜는 필수입니다")
    private LocalDate sermonDate;

    @Size(max = 200, message = "성경 구절은 200자 이하여야 합니다")
    private String bibleVerse;

    private String summary;

    @Size(max = 50, message = "YouTube Video ID는 50자 이하여야 합니다")
    private String youtubeVideoId;

    @Size(max = 500, message = "비디오 URL은 500자 이하여야 합니다")
    private String videoUrl;

    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    private String thumbnailUrl;

    private Integer duration;

    @Size(max = 500, message = "태그는 500자 이하여야 합니다")
    private String tags;

    private String description;

    @NotNull(message = "추천 여부는 필수입니다")
    @Builder.Default
    private Boolean isFeatured = false;

    @NotNull(message = "공개 여부는 필수입니다")
    @Builder.Default
    private Boolean isPublished = false;
}
