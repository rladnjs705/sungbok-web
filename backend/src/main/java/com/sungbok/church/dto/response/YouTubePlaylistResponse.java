package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.YouTubePlaylist;
import lombok.*;

import java.time.LocalDateTime;

/**
 * YouTubePlaylist 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubePlaylistResponse {

    private Long id;
    private String playlistId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer videoCount;
    private String category;
    private LocalDateTime lastSyncedAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static YouTubePlaylistResponse from(YouTubePlaylist playlist) {
        return YouTubePlaylistResponse.builder()
            .id(playlist.getId())
            .playlistId(playlist.getPlaylistId())
            .title(playlist.getTitle())
            .description(playlist.getDescription())
            .thumbnailUrl(playlist.getThumbnailUrl())
            .videoCount(playlist.getVideoCount())
            .category(playlist.getCategory())
            .lastSyncedAt(playlist.getLastSyncedAt())
            .isActive(playlist.getIsActive())
            .createdAt(playlist.getCreatedAt())
            .updatedAt(playlist.getUpdatedAt())
            .build();
    }
}
