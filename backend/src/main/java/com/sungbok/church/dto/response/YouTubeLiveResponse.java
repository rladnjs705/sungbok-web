package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * YouTubeLive 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubeLiveResponse {

    private Long id;
    private Long worshipId;
    private String worshipName;
    private String youtubeVideoId;
    private String title;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private LiveStatus status;
    private Integer viewerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static YouTubeLiveResponse from(YouTubeLive live) {
        return YouTubeLiveResponse.builder()
            .id(live.getId())
            .worshipId(live.getWorship() != null ? live.getWorship().getId() : null)
            .worshipName(live.getWorship() != null ? live.getWorship().getTitle() : null)
            .youtubeVideoId(live.getYoutubeVideoId())
            .title(live.getTitle())
            .scheduledStartTime(live.getScheduledStartTime())
            .actualStartTime(live.getActualStartTime())
            .endTime(live.getEndTime())
            .status(live.getStatus())
            .viewerCount(live.getViewerCount())
            .createdAt(live.getCreatedAt())
            .updatedAt(live.getUpdatedAt())
            .build();
    }
}
