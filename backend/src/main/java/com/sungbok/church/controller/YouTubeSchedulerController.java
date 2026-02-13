package com.sungbok.church.controller;

import com.sungbok.church.scheduler.SmartYouTubeScheduler;
import com.sungbok.church.scheduler.YouTubeScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * YouTube Scheduler Controller
 * 스케줄 작업을 수동으로 트리거하기 위한 관리자 API
 *
 * 용도:
 * - 스케줄 시간을 기다리지 않고 즉시 실행
 * - 디버깅 및 테스트
 *
 * Note: 프로덕션 환경에서는 관리자 권한 체크 필요
 */
@RestController
@RequestMapping("/api/admin/youtube-scheduler")
@RequiredArgsConstructor
@Slf4j
public class YouTubeSchedulerController {

    private final YouTubeScheduler youTubeScheduler;
    private final SmartYouTubeScheduler smartYouTubeScheduler;

    /**
     * 라이브 스캔 수동 트리거
     */
    @PostMapping("/scan-live")
    public ResponseEntity<Map<String, Object>> triggerLiveScan() {
        log.info("Manual trigger: Live stream scan");

        try {
            smartYouTubeScheduler.scanLiveStreams();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Live stream scan completed");
            response.put("task", "scanLiveStreams");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to trigger live scan", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Live scan failed: " + e.getMessage());
            response.put("task", "scanLiveStreams");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 영상 동기화 수동 트리거
     */
    @PostMapping("/sync-videos")
    public ResponseEntity<Map<String, Object>> triggerVideoSync() {
        log.info("Manual trigger: Video sync");

        try {
            youTubeScheduler.syncLatestVideos();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Video sync completed");
            response.put("task", "syncLatestVideos");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to trigger video sync", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Video sync failed: " + e.getMessage());
            response.put("task", "syncLatestVideos");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 재생목록 동기화 수동 트리거
     */
    @PostMapping("/sync-playlists")
    public ResponseEntity<Map<String, Object>> triggerPlaylistSync() {
        log.info("Manual trigger: Playlist sync");

        try {
            youTubeScheduler.syncPlaylists();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Playlist sync completed");
            response.put("task", "syncPlaylists");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to trigger playlist sync", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Playlist sync failed: " + e.getMessage());
            response.put("task", "syncPlaylists");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 비활성 라이브 정리 수동 트리거
     */
    @PostMapping("/cleanup-inactive")
    public ResponseEntity<Map<String, Object>> triggerCleanup() {
        log.info("Manual trigger: Cleanup inactive lives");

        try {
            youTubeScheduler.cleanupInactiveLives();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cleanup completed");
            response.put("task", "cleanupInactiveLives");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to trigger cleanup", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cleanup failed: " + e.getMessage());
            response.put("task", "cleanupInactiveLives");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 모든 스케줄 작업 한번에 실행 (테스트용)
     */
    @PostMapping("/run-all")
    public ResponseEntity<Map<String, Object>> triggerAll() {
        log.info("Manual trigger: Run all scheduled tasks");

        Map<String, Object> response = new HashMap<>();
        Map<String, String> results = new HashMap<>();

        try {
            // 1. Live scan
            smartYouTubeScheduler.scanLiveStreams();
            results.put("scanLiveStreams", "SUCCESS");

        } catch (Exception e) {
            results.put("scanLiveStreams", "FAILED: " + e.getMessage());
            log.error("Live scan failed", e);
        }

        try {
            // 2. Video sync
            youTubeScheduler.syncLatestVideos();
            results.put("syncLatestVideos", "SUCCESS");

        } catch (Exception e) {
            results.put("syncLatestVideos", "FAILED: " + e.getMessage());
            log.error("Video sync failed", e);
        }

        try {
            // 3. Playlist sync
            youTubeScheduler.syncPlaylists();
            results.put("syncPlaylists", "SUCCESS");

        } catch (Exception e) {
            results.put("syncPlaylists", "FAILED: " + e.getMessage());
            log.error("Playlist sync failed", e);
        }

        try {
            // 4. Cleanup
            youTubeScheduler.cleanupInactiveLives();
            results.put("cleanupInactiveLives", "SUCCESS");

        } catch (Exception e) {
            results.put("cleanupInactiveLives", "FAILED: " + e.getMessage());
            log.error("Cleanup failed", e);
        }

        response.put("success", true);
        response.put("message", "All scheduled tasks executed");
        response.put("results", results);

        return ResponseEntity.ok(response);
    }

    /**
     * 스케줄러 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Scheduler is active");
        response.put("tasks", Map.of(
            "scanLiveStreams", "Smart scheduling (5min during worship, 30min otherwise)",
            "syncLatestVideos", "Every hour (cron: 0 0 * * * *)",
            "syncPlaylists", "Every 6 hours (cron: 0 0 */6 * * *)",
            "cleanupInactiveLives", "Every 30 minutes (cron: 0 */30 * * * *)"
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Health Check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("YouTube Scheduler Controller is running");
    }
}
