package com.sungbok.church.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * YouTubePlaylist 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubePlaylistRequest {

    @NotBlank(message = "재생목록 ID는 필수입니다")
    @Size(max = 50, message = "재생목록 ID는 50자 이하여야 합니다")
    private String playlistId;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    private String description;

    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    private String thumbnailUrl;

    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    private String category;

    @NotNull(message = "자동 동기화 여부는 필수입니다")
    @Builder.Default
    private Boolean autoSync = false;

    @NotNull(message = "표시 순서는 필수입니다")
    @Builder.Default
    private Integer displayOrder = 0;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Builder.Default
    private Boolean isActive = true;
}
