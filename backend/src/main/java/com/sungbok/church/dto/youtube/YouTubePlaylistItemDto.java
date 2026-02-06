package com.sungbok.church.dto.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * YouTube PlaylistItem DTO
 * YouTube Data API v3 playlistItems response 매핑
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubePlaylistItemDto {

    private String kind;
    private String etag;
    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private String publishedAt;
        private String channelId;
        private String title;
        private String description;
        private Thumbnails thumbnails;
        private String channelTitle;
        private String playlistId;
        private Integer position;
        private ResourceId resourceId;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Thumbnails {
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

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ResourceId {
            private String kind;
            private String videoId;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentDetails {
        private String videoId;
        private String videoPublishedAt;
    }

    /**
     * 헬퍼 메서드: Video ID 가져오기
     */
    public String getVideoId() {
        if (contentDetails != null && contentDetails.videoId != null) {
            return contentDetails.videoId;
        }
        if (snippet != null && snippet.resourceId != null) {
            return snippet.resourceId.videoId;
        }
        return null;
    }

    /**
     * 헬퍼 메서드: 썸네일 URL 가져오기
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
}
