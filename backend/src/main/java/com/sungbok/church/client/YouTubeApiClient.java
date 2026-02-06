package com.sungbok.church.client;

import com.sungbok.church.config.YouTubeConfig;
import com.sungbok.church.dto.youtube.*;
import com.sungbok.church.exception.YouTubeApiException;
import com.sungbok.church.util.YouTubeQuotaTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

/**
 * YouTube API Client
 * YouTube Data API v3와 통신하는 클라이언트
 *
 * API 할당량 (Quota):
 * - search.list: 100 units
 * - videos.list: 1 unit
 * - channels.list: 1 unit
 * - playlistItems.list: 1 unit
 *
 * Phase 4 최적화:
 * - Quota tracking with YouTubeQuotaTracker
 * - Caching with Spring Cache (Caffeine)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class YouTubeApiClient {

    private final RestTemplate restTemplate;
    private final YouTubeConfig youTubeConfig;
    private final YouTubeQuotaTracker quotaTracker;

    private static final String SEARCH_ENDPOINT = "/search";
    private static final String VIDEOS_ENDPOINT = "/videos";
    private static final String CHANNELS_ENDPOINT = "/channels";
    private static final String PLAYLIST_ITEMS_ENDPOINT = "/playlistItems";

    /**
     * 1. 최신 영상 조회
     *
     * @param maxResults 최대 결과 수 (기본: 50)
     * @return 영상 목록
     * @throws YouTubeApiException API 호출 실패 시
     */
    public List<YouTubeVideoDto> fetchLatestVideos(Integer maxResults) {
        log.debug("Fetching latest videos from channel: {}", youTubeConfig.getApi().getChannelId());

        // Quota check: search.list (100 units) + videos.list (1 unit)
        int estimatedCost = YouTubeQuotaTracker.SEARCH_COST + YouTubeQuotaTracker.VIDEO_COST;
        quotaTracker.checkQuota(estimatedCost);

        try {
            // Step 1: search.list로 최신 영상 ID 조회
            quotaTracker.recordUsage(YouTubeQuotaTracker.SEARCH_COST);

            String searchUrl = buildUrl(SEARCH_ENDPOINT)
                .queryParam("part", "snippet")
                .queryParam("channelId", youTubeConfig.getApi().getChannelId())
                .queryParam("order", "date")
                .queryParam("type", "video")
                .queryParam("maxResults", maxResults != null ? maxResults : youTubeConfig.getApi().getMaxResults())
                .toUriString();

            ResponseEntity<YouTubeApiResponse<YouTubeVideoDto>> searchResponse = restTemplate.exchange(
                searchUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<YouTubeApiResponse<YouTubeVideoDto>>() {}
            );

            YouTubeApiResponse<YouTubeVideoDto> searchResult = searchResponse.getBody();
            if (searchResult == null || !searchResult.hasItems()) {
                log.warn("No videos found for channel: {}", youTubeConfig.getApi().getChannelId());
                return Collections.emptyList();
            }

            // Step 2: videos.list로 상세 정보 조회
            List<String> videoIds = searchResult.getItems().stream()
                .map(video -> video.getId())
                .filter(id -> id != null && !id.isEmpty())
                .toList();

            if (videoIds.isEmpty()) {
                return Collections.emptyList();
            }

            return fetchVideoDetailsByIds(videoIds);

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Invalid YouTube API Key", e);
            throw YouTubeApiException.invalidApiKey("Invalid YouTube API Key: " + e.getMessage());
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("YouTube API quota exceeded", e);
            throw YouTubeApiException.quotaExceeded("YouTube API quota exceeded: " + e.getMessage());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("YouTube API error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new YouTubeApiException("YouTube API error: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("YouTube API timeout or network error", e);
            throw YouTubeApiException.timeout("YouTube API timeout: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching latest videos", e);
            throw new YouTubeApiException("Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * 2. 영상 상세 정보 조회 (단일)
     *
     * @param videoId YouTube Video ID
     * @return 영상 상세 정보
     * @throws YouTubeApiException API 호출 실패 시
     */
    public YouTubeVideoDto fetchVideoDetails(String videoId) {
        log.debug("Fetching video details for: {}", videoId);

        List<YouTubeVideoDto> videos = fetchVideoDetailsByIds(List.of(videoId));
        if (videos.isEmpty()) {
            throw YouTubeApiException.notFound("Video not found: " + videoId);
        }
        return videos.get(0);
    }

    /**
     * 3. 라이브 방송 목록 조회
     * 캐싱: 1분 (youtubeLive 캐시)
     *
     * @return 라이브 방송 목록 (현재 진행 중 + 예정)
     * @throws YouTubeApiException API 호출 실패 시
     */
    @Cacheable(value = "youtubeLive", key = "'liveStreams'")
    public List<YouTubeVideoDto> fetchLiveStreams() {
        log.debug("Fetching live streams from channel: {}", youTubeConfig.getApi().getChannelId());

        // Quota check: search.list (100 units) + videos.list (1 unit)
        int estimatedCost = YouTubeQuotaTracker.SEARCH_COST + YouTubeQuotaTracker.VIDEO_COST;
        quotaTracker.checkQuota(estimatedCost);

        try {
            // search.list로 라이브 방송 조회
            quotaTracker.recordUsage(YouTubeQuotaTracker.SEARCH_COST);

            String searchUrl = buildUrl(SEARCH_ENDPOINT)
                .queryParam("part", "snippet")
                .queryParam("channelId", youTubeConfig.getApi().getChannelId())
                .queryParam("eventType", "live")  // 현재 라이브
                .queryParam("type", "video")
                .queryParam("maxResults", 25)
                .toUriString();

            ResponseEntity<YouTubeApiResponse<YouTubeVideoDto>> response = restTemplate.exchange(
                searchUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<YouTubeApiResponse<YouTubeVideoDto>>() {}
            );

            YouTubeApiResponse<YouTubeVideoDto> result = response.getBody();
            if (result == null || !result.hasItems()) {
                log.info("No live streams found");
                return Collections.emptyList();
            }

            // 상세 정보 조회
            List<String> videoIds = result.getItems().stream()
                .map(YouTubeVideoDto::getId)
                .filter(id -> id != null && !id.isEmpty())
                .toList();

            return fetchVideoDetailsByIds(videoIds);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching live streams: {}", e.getMessage(), e);
            throw new YouTubeApiException("Error fetching live streams: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("Network error fetching live streams", e);
            throw YouTubeApiException.timeout("Network error: " + e.getMessage(), e);
        }
    }

    /**
     * 4. 재생목록 영상 조회
     *
     * @param playlistId YouTube Playlist ID
     * @param maxResults 최대 결과 수
     * @return 재생목록 영상 목록
     * @throws YouTubeApiException API 호출 실패 시
     */
    public List<YouTubePlaylistItemDto> fetchPlaylistItems(String playlistId, Integer maxResults) {
        log.debug("Fetching playlist items for: {}", playlistId);

        try {
            String url = buildUrl(PLAYLIST_ITEMS_ENDPOINT)
                .queryParam("part", "snippet,contentDetails")
                .queryParam("playlistId", playlistId)
                .queryParam("maxResults", maxResults != null ? maxResults : youTubeConfig.getApi().getMaxResults())
                .toUriString();

            ResponseEntity<YouTubeApiResponse<YouTubePlaylistItemDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<YouTubeApiResponse<YouTubePlaylistItemDto>>() {}
            );

            YouTubeApiResponse<YouTubePlaylistItemDto> result = response.getBody();
            if (result == null || !result.hasItems()) {
                log.warn("No items found in playlist: {}", playlistId);
                return Collections.emptyList();
            }

            log.info("Fetched {} items from playlist: {}", result.getItemCount(), playlistId);
            return result.getItems();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching playlist items: {}", e.getMessage(), e);
            throw new YouTubeApiException("Error fetching playlist: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("Network error fetching playlist", e);
            throw YouTubeApiException.timeout("Network error: " + e.getMessage(), e);
        }
    }

    /**
     * 5. 채널 정보 조회
     * 캐싱: 24시간 (youtubeChannel 캐시)
     *
     * @return 채널 정보
     * @throws YouTubeApiException API 호출 실패 시
     */
    @Cacheable(value = "youtubeChannel", key = "'channelInfo'")
    public YouTubeChannelDto fetchChannelInfo() {
        log.debug("Fetching channel info for: {}", youTubeConfig.getApi().getChannelId());

        // Quota check: channels.list (1 unit)
        quotaTracker.checkQuota(YouTubeQuotaTracker.CHANNEL_COST);

        try {
            quotaTracker.recordUsage(YouTubeQuotaTracker.CHANNEL_COST);

            String url = buildUrl(CHANNELS_ENDPOINT)
                .queryParam("part", "snippet,contentDetails,statistics")
                .queryParam("id", youTubeConfig.getApi().getChannelId())
                .toUriString();

            ResponseEntity<YouTubeApiResponse<YouTubeChannelDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<YouTubeApiResponse<YouTubeChannelDto>>() {}
            );

            YouTubeApiResponse<YouTubeChannelDto> result = response.getBody();
            if (result == null || !result.hasItems()) {
                throw YouTubeApiException.notFound("Channel not found: " + youTubeConfig.getApi().getChannelId());
            }

            YouTubeChannelDto channel = result.getItems().get(0);
            log.info("Fetched channel info: {}", channel.getSnippet().getTitle());
            return channel;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching channel info: {}", e.getMessage(), e);
            throw new YouTubeApiException("Error fetching channel: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("Network error fetching channel", e);
            throw YouTubeApiException.timeout("Network error: " + e.getMessage(), e);
        }
    }

    // ===== Private Helper Methods =====

    /**
     * 여러 Video ID로 상세 정보 조회 (Batch)
     */
    private List<YouTubeVideoDto> fetchVideoDetailsByIds(List<String> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // Record quota: videos.list costs 1 unit per call (not per video)
            quotaTracker.recordUsage(YouTubeQuotaTracker.VIDEO_COST);

            String url = buildUrl(VIDEOS_ENDPOINT)
                .queryParam("part", "snippet,contentDetails,statistics,liveStreamingDetails")
                .queryParam("id", String.join(",", videoIds))
                .toUriString();

            ResponseEntity<YouTubeApiResponse<YouTubeVideoDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<YouTubeApiResponse<YouTubeVideoDto>>() {}
            );

            YouTubeApiResponse<YouTubeVideoDto> result = response.getBody();
            if (result == null || !result.hasItems()) {
                return Collections.emptyList();
            }

            log.debug("Fetched {} video details", result.getItemCount());
            return result.getItems();

        } catch (Exception e) {
            log.error("Error fetching video details by IDs", e);
            throw new YouTubeApiException("Error fetching video details: " + e.getMessage(), e);
        }
    }

    /**
     * API URL 빌더
     */
    private UriComponentsBuilder buildUrl(String endpoint) {
        return UriComponentsBuilder
            .fromUriString(youTubeConfig.getApi().getBaseUrl() + endpoint)
            .queryParam("key", youTubeConfig.getApi().getKey());
    }
}
