package com.sungbok.church.controller;

import com.sungbok.church.service.YouTubeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * YouTube Sync Test Controller
 * YouTubeSyncService 기능 테스트용 임시 컨트롤러
 *
 * TODO: Phase 4 완료 후 삭제 또는 비활성화
 */
@RestController
@RequestMapping("/api/test/youtube-sync")
@RequiredArgsConstructor
@Slf4j
public class YouTubeSyncTestController {

    private final YouTubeSyncService youtubeSyncService;

    /**
     * Test 1: 최신 영상 동기화
     */
    @PostMapping("/sync-videos")
    public ResponseEntity<Map<String, Object>> testSyncVideos(
            @RequestParam(defaultValue = "10") Integer maxResults) {
        log.info("Testing syncLatestVideos with maxResults: {}", maxResults);

        int syncedCount = youtubeSyncService.syncLatestVideos(maxResults);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("syncedCount", syncedCount);
        response.put("message", syncedCount + " sermons synced successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Test 2: 라이브 상태 업데이트
     */
    @PostMapping("/update-live")
    public ResponseEntity<Map<String, Object>> testUpdateLiveStatus() {
        log.info("Testing updateLiveStatus");

        int updatedCount = youtubeSyncService.updateLiveStatus();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("updatedCount", updatedCount);
        response.put("message", updatedCount + " live streams updated");

        return ResponseEntity.ok(response);
    }

    /**
     * Test 3: 재생목록 동기화
     */
    @PostMapping("/sync-playlist/{playlistId}")
    public ResponseEntity<Map<String, Object>> testSyncPlaylist(
            @PathVariable String playlistId,
            @RequestParam(defaultValue = "예배영상") String category) {
        log.info("Testing syncPlaylist: {} (category: {})", playlistId, category);

        int syncedCount = youtubeSyncService.syncPlaylist(playlistId, category);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("syncedCount", syncedCount);
        response.put("category", category);
        response.put("message", syncedCount + " videos synced to gallery");

        return ResponseEntity.ok(response);
    }

    /**
     * Test 4: 제목에서 날짜 파싱
     */
    @GetMapping("/parse-date")
    public ResponseEntity<Map<String, Object>> testParseDateFromTitle(
            @RequestParam String title) {
        log.info("Testing parseDateFromTitle: {}", title);

        LocalDate date = youtubeSyncService.parseDateFromTitle(title);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("title", title);
        response.put("parsedDate", date.toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Test 5: 제목에서 설교자 파싱
     */
    @GetMapping("/parse-preacher")
    public ResponseEntity<Map<String, Object>> testParsePreacherFromTitle(
            @RequestParam String title) {
        log.info("Testing parsePreacherFromTitle: {}", title);

        String preacher = youtubeSyncService.parsePreacherFromTitle(title);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("title", title);
        response.put("parsedPreacher", preacher);

        return ResponseEntity.ok(response);
    }

    /**
     * Health Check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("YouTube Sync Service is running");
    }
}
