package com.sungbok.church.dto.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * YouTube API Response Wrapper
 * YouTube Data API v3 공통 응답 구조
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeApiResponse<T> {

    private String kind;
    private String etag;
    private String nextPageToken;
    private String prevPageToken;
    private PageInfo pageInfo;
    private List<T> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageInfo {
        private Integer totalResults;
        private Integer resultsPerPage;
    }

    /**
     * 헬퍼 메서드: 다음 페이지 존재 여부
     */
    public boolean hasNextPage() {
        return nextPageToken != null && !nextPageToken.isEmpty();
    }

    /**
     * 헬퍼 메서드: 결과 존재 여부
     */
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    /**
     * 헬퍼 메서드: 결과 개수
     */
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
