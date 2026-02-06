package com.sungbok.church.dto.request;

import com.sungbok.church.domain.enums.LiveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * YouTubeLive 생성/수정 Request DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubeLiveRequest {

    private Long worshipId;

    @NotBlank(message = "YouTube Video ID는 필수입니다")
    @Size(max = 50, message = "YouTube Video ID는 50자 이하여야 합니다")
    private String youtubeVideoId;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    private String description;

    @Size(max = 500, message = "썸네일 URL은 500자 이하여야 합니다")
    private String thumbnailUrl;

    private LocalDateTime scheduledStartTime;

    @NotNull(message = "상태는 필수입니다")
    @Builder.Default
    private LiveStatus status = LiveStatus.SCHEDULED;
}
