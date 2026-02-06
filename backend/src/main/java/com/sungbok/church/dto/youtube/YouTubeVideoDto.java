package com.sungbok.church.dto.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * YouTube Video DTO
 * YouTube Data API v3 video response 매핑
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeVideoDto {

    private String kind;
    private String etag;
    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;
    private Statistics statistics;
    private LiveStreamingDetails liveStreamingDetails;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private String publishedAt;
        private String channelId;
        private String title;
        private String description;
        private Thumbnails thumbnails;
        private String channelTitle;
        private List<String> tags;
        private String categoryId;
        private String liveBroadcastContent;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Thumbnails {
            @JsonProperty("default")
            private Thumbnail defaultThumbnail;
            private Thumbnail medium;
            private Thumbnail high;
            private Thumbnail standard;
            private Thumbnail maxres;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Thumbnail {
                private String url;
                private Integer width;
                private Integer height;
            }
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentDetails {
        private String duration;
        private String dimension;
        private String definition;
        private String caption;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistics {
        private String viewCount;
        private String likeCount;
        private String commentCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveStreamingDetails {
        private String actualStartTime;
        private String actualEndTime;
        private String scheduledStartTime;
        private String concurrentViewers;
    }

    /**
     * 헬퍼 메서드: 썸네일 URL 가져오기 (우선순위: maxres > standard > high > medium > default)
     */
    public String getThumbnailUrl() {
        if (snippet == null || snippet.thumbnails == null) {
            return null;
        }
        Snippet.Thumbnails thumbs = snippet.thumbnails;
        if (thumbs.maxres != null) return thumbs.maxres.getUrl();
        if (thumbs.standard != null) return thumbs.standard.getUrl();
        if (thumbs.high != null) return thumbs.high.getUrl();
        if (thumbs.medium != null) return thumbs.medium.getUrl();
        if (thumbs.defaultThumbnail != null) return thumbs.defaultThumbnail.getUrl();
        return null;
    }

    /**
     * 헬퍼 메서드: 라이브 방송 여부 확인
     */
    public boolean isLive() {
        return snippet != null && "live".equals(snippet.liveBroadcastContent);
    }

    /**
     * 헬퍼 메서드: 예정된 라이브 방송 여부 확인
     */
    public boolean isUpcoming() {
        return snippet != null && "upcoming".equals(snippet.liveBroadcastContent);
    }
}
