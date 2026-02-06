package com.sungbok.church.controller;

import com.sungbok.church.client.YouTubeApiClient;
import com.sungbok.church.dto.youtube.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * YouTube API Test Controller
 * YouTubeApiClient 기능 테스트용 임시 컨트롤러
 *
 * TODO: Phase 4 완료 후 삭제 또는 비활성화
 */
@RestController
@RequestMapping("/api/test/youtube")
@RequiredArgsConstructor
@Slf4j
public class YouTubeTestController {

    private final YouTubeApiClient youTubeApiClient;

    /**
     * Test 1: 최신 영상 조회
     */
    @GetMapping("/latest-videos")
    public ResponseEntity<List<YouTubeVideoDto>> testFetchLatestVideos(
            @RequestParam(defaultValue = "10") Integer maxResults) {
        log.info("Testing fetchLatestVideos with maxResults: {}", maxResults);
        List<YouTubeVideoDto> videos = youTubeApiClient.fetchLatestVideos(maxResults);
        return ResponseEntity.ok(videos);
    }

    /**
     * Test 2: 영상 상세 정보 조회
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<YouTubeVideoDto> testFetchVideoDetails(@PathVariable String videoId) {
        log.info("Testing fetchVideoDetails for videoId: {}", videoId);
        YouTubeVideoDto video = youTubeApiClient.fetchVideoDetails(videoId);
        return ResponseEntity.ok(video);
    }

    /**
     * Test 3: 라이브 방송 목록 조회
     */
    @GetMapping("/live-streams")
    public ResponseEntity<List<YouTubeVideoDto>> testFetchLiveStreams() {
        log.info("Testing fetchLiveStreams");
        List<YouTubeVideoDto> liveStreams = youTubeApiClient.fetchLiveStreams();
        return ResponseEntity.ok(liveStreams);
    }

    /**
     * Test 4: 재생목록 영상 조회
     */
    @GetMapping("/playlist/{playlistId}")
    public ResponseEntity<List<YouTubePlaylistItemDto>> testFetchPlaylistItems(
            @PathVariable String playlistId,
            @RequestParam(defaultValue = "25") Integer maxResults) {
        log.info("Testing fetchPlaylistItems for playlistId: {}", playlistId);
        List<YouTubePlaylistItemDto> items = youTubeApiClient.fetchPlaylistItems(playlistId, maxResults);
        return ResponseEntity.ok(items);
    }

    /**
     * Test 5: 채널 정보 조회
     */
    @GetMapping("/channel-info")
    public ResponseEntity<YouTubeChannelDto> testFetchChannelInfo() {
        log.info("Testing fetchChannelInfo");
        YouTubeChannelDto channel = youTubeApiClient.fetchChannelInfo();
        return ResponseEntity.ok(channel);
    }

    /**
     * Health Check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("YouTube API Client is running");
    }
}
