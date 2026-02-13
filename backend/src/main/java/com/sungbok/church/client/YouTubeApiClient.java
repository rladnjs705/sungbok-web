package com.sungbok.church.client;

import com.sungbok.church.config.YouTubeConfig;
import com.sungbok.church.dto.youtube.*;
import com.sungbok.church.util.YouTubeQuotaTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * YouTube API Client
 * YouTube Data API v3와 통신하는 클라이언트
 *
 * Resilience 개선 (2026-02-11):
 * - API 키 invalid 시 graceful degradation (빈 결과 반환)
 * - API 키 상태 캐싱 (1시간)
 * - 예외 로깅 후 계속 진행
 * - @Cacheable 제거: 예외 발생 시 캐시 프록시 문제 방지
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

    // API 키 상태 캐싱 (1시간)
    private static final long API_KEY_CACHE_MILLIS = 60 * 60 * 1000;
    private final AtomicLong lastApiKeyCheck = new AtomicLong(0);
    private final AtomicBoolean isApiKeyValid = new AtomicBoolean(true);

    /**
     * API 키 유효성 체크 (캐싱)
     */
    private boolean isApiKeyValid() {
        long now = System.currentTimeMillis();
        long lastCheck = lastApiKeyCheck.get();

        if (now - lastCheck < API_KEY_CACHE_MILLIS) {
            return isApiKeyValid.get();
        }

        try {
            String testUrl = buildUrl(CHANNELS_ENDPOINT)
                .queryParam("part", "id")
                .queryParam("id", youTubeConfig.getApi().getChannelId())
                .toUriString();

            restTemplate.exchange(
                testUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<YouTubeApiResponse<YouTubeChannelDto>>() {}
            );

            isApiKeyValid.set(true);
            lastApiKeyCheck.set(now);
            return true;

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized | 
                 HttpClientErrorException.Forbidden e) {
            log.error("YouTube API Key is invalid or expired. All API calls will return empty results for 1 hour.");
            isApiKeyValid.set(false);
            lastApiKeyCheck.set(now);
            return false;
        } catch (Exception e) {
            log.warn("Failed to check YouTube API key status: {}", e.getMessage());
            return isApiKeyValid.get();
        }
    }

    private boolean shouldSkipApiCall() {
        if (!isApiKeyValid()) {
            log.debug("Skipping YouTube API call - API key is invalid (cached)");
            return true;
        }
        return false;
    }

    /**
     * 최신 영상 조회 - 예외 발생 시 빈 리스트 반환
     */
    public List<YouTubeVideoDto> fetchLatestVideos(Integer maxResults) {
        log.debug("Fetching latest videos from channel: {}", youTubeConfig.getApi().getChannelId());

        if (shouldSkipApiCall()) {
            return Collections.emptyList();
        }

        try {
            int estimatedCost = YouTubeQuotaTracker.SEARCH_COST + YouTubeQuotaTracker.VIDEO_COST;
            quotaTracker.checkQuota(estimatedCost);
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

            List<String> videoIds = searchResult.getItems().stream()
                .map(video -> video.getId())
                .filter(id -> id != null && !id.isEmpty())
                .toList();

            if (videoIds.isEmpty()) {
                return Collections.emptyList();
            }

            return fetchVideoDetailsByIds(videoIds);

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized | 
                 HttpClientErrorException.Forbidden e) {
            log.error("YouTube API Key is invalid ({}). Marking as invalid for 1 hour.", e.getStatusCode());
            isApiKeyValid.set(false);
            lastApiKeyCheck.set(System.currentTimeMillis());
            return Collections.emptyList();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("YouTube API quota exceeded. Returning empty results.");
            return Collections.emptyList();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("YouTube API error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (ResourceAccessException e) {
            log.error("YouTube API timeout or network error: {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error fetching latest videos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 영상 상세 정보 조회 (단일) - 예외 발생 시 null 반환
     */
    public YouTubeVideoDto fetchVideoDetails(String videoId) {
        log.debug("Fetching video details for: {}", videoId);

        if (shouldSkipApiCall()) {
            return null;
        }

        try {
            List<YouTubeVideoDto> videos = fetchVideoDetailsByIds(List.of(videoId));
            return videos.isEmpty() ? null : videos.get(0);
        } catch (Exception e) {
            log.error("Error fetching video details for {}: {}", videoId, e.getMessage());
            return null;
        }
    }

    /**
     * 라이브 방송 목록 조회 - 예외 발생 시 빈 리스트 반환
     * @Cacheable 제거: 캐시 프록시가 예외를 감싸서 던지는 문제 방지
     */
    public List<YouTubeVideoDto> fetchLiveStreams() {
        log.debug("Fetching live streams from channel: {}", youTubeConfig.getApi().getChannelId());

        if (shouldSkipApiCall()) {
            return Collections.emptyList();
        }

        try {
            int estimatedCost = YouTubeQuotaTracker.SEARCH_COST + YouTubeQuotaTracker.VIDEO_COST;
            quotaTracker.checkQuota(estimatedCost);
            quotaTracker.recordUsage(YouTubeQuotaTracker.SEARCH_COST);

            String searchUrl = buildUrl(SEARCH_ENDPOINT)
                .queryParam("part", "snippet")
                .queryParam("channelId", youTubeConfig.getApi().getChannelId())
                .queryParam("eventType", "live")
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

            List<String> videoIds = result.getItems().stream()
                .map(YouTubeVideoDto::getId)
                .filter(id -> id != null && !id.isEmpty())
                .toList();

            return fetchVideoDetailsByIds(videoIds);

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized | 
                 HttpClientErrorException.Forbidden e) {
            log.error("YouTube API Key is invalid ({}). Marking as invalid for 1 hour.", e.getStatusCode());
            isApiKeyValid.set(false);
            lastApiKeyCheck.set(System.currentTimeMillis());
            return Collections.emptyList();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching live streams: {}", e.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException e) {
            log.error("Network error fetching live streams: {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error fetching live streams: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 재생목록 영상 조회 - 예외 발생 시 빈 리스트 반환
     */
    public List<YouTubePlaylistItemDto> fetchPlaylistItems(String playlistId, Integer maxResults) {
        log.debug("Fetching playlist items for: {}", playlistId);

        if (shouldSkipApiCall()) {
            return Collections.emptyList();
        }

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

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized | 
                 HttpClientErrorException.Forbidden e) {
            log.error("YouTube API Key is invalid ({}). Marking as invalid for 1 hour.", e.getStatusCode());
            isApiKeyValid.set(false);
            lastApiKeyCheck.set(System.currentTimeMillis());
            return Collections.emptyList();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching playlist items: {}", e.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException e) {
            log.error("Network error fetching playlist: {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error fetching playlist items: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 채널 정보 조회 - 예외 발생 시 null 반환
     * @Cacheable 제거
     */
    public YouTubeChannelDto fetchChannelInfo() {
        log.debug("Fetching channel info for: {}", youTubeConfig.getApi().getChannelId());

        if (shouldSkipApiCall()) {
            return null;
        }

        try {
            quotaTracker.checkQuota(YouTubeQuotaTracker.CHANNEL_COST);
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
                log.warn("Channel not found: {}", youTubeConfig.getApi().getChannelId());
                return null;
            }

            YouTubeChannelDto channel = result.getItems().get(0);
            log.info("Fetched channel info: {}", channel.getSnippet().getTitle());
            return channel;

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized | 
                 HttpClientErrorException.Forbidden e) {
            log.error("YouTube API Key is invalid ({}). Marking as invalid for 1 hour.", e.getStatusCode());
            isApiKeyValid.set(false);
            lastApiKeyCheck.set(System.currentTimeMillis());
            return null;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error fetching channel info: {}", e.getMessage());
            return null;
        } catch (ResourceAccessException e) {
            log.error("Network error fetching channel: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error fetching channel info: {}", e.getMessage());
            return null;
        }
    }

    // Private Helper Methods

    private List<YouTubeVideoDto> fetchVideoDetailsByIds(List<String> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
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

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized | 
                 HttpClientErrorException.Forbidden e) {
            log.error("YouTube API Key is invalid ({}).", e.getStatusCode());
            isApiKeyValid.set(false);
            lastApiKeyCheck.set(System.currentTimeMillis());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching video details by IDs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private UriComponentsBuilder buildUrl(String endpoint) {
        return UriComponentsBuilder
            .fromUriString(youTubeConfig.getApi().getBaseUrl() + endpoint)
            .queryParam("key", youTubeConfig.getApi().getKey());
    }
}
