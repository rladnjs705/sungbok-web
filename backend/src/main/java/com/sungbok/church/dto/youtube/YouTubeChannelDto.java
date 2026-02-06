package com.sungbok.church.dto.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * YouTube Channel DTO
 * YouTube Data API v3 channel response 매핑
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeChannelDto {

    private String kind;
    private String etag;
    private String id;
    private Snippet snippet;
    private ContentDetails contentDetails;
    private Statistics statistics;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private String title;
        private String description;
        private String customUrl;
        private String publishedAt;
        private Thumbnails thumbnails;
        private String country;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Thumbnails {
            private Thumbnail defaultThumbnail;
            private Thumbnail medium;
            private Thumbnail high;

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
        private RelatedPlaylists relatedPlaylists;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RelatedPlaylists {
            private String uploads;
            private String likes;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistics {
        private String viewCount;
        private String subscriberCount;
        private String videoCount;
    }
}
