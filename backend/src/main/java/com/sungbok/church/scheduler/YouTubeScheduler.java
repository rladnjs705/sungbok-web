package com.sungbok.church.scheduler;

import com.sungbok.church.config.YouTubeConfig;
import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import com.sungbok.church.exception.YouTubeApiException;
import com.sungbok.church.service.YouTubeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * YouTube Scheduler
 * YouTube API 자동 동기화 스케줄러
 *
 * 주요 작업:
 * 1. 영상 동기화 (매시간)
 * 2. 재생목록 동기화 (6시간마다)
 * 3. 비활성 라이브 정리 (30분마다)
 *
 * 라이브 스캔은 SmartYouTubeScheduler에서 담당
 *
 * Resilience 개선 (2026-02-11):
 * - 재시도 로직 개선: 401/403 오류는 즉시 실패
 * - API 키 invalid 시 graceful degradation
 * - 모든 예외를 catch하여 서비스 중단 방지
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class YouTubeScheduler {

    private final YouTubeSyncService youtubeSyncService;
    private final YouTubeLiveRepository youtubeLiveRepository;
    private final YouTubeConfig youTubeConfig;

    // 재시도 횟수
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Task 1: 최신 영상 동기화 (SmartYouTubeScheduler의 라이브 스캔과 분리)
     * YouTube 채널의 최신 영상을 가져와 Sermon 테이블에 저장
     *
     * 실행 주기: 매시간 정각 (cron = "0 0 * * * *")
     * Quota Cost: ~100 units/call
     * Daily Cost: 24 calls × 100 units = 2,400 units
     */
    @Scheduled(cron = "${youtube.sync.video-sync-cron:0 0 * * * *}")
    public void syncLatestVideos() {
        // Sync 비활성화 체크
        if (!youTubeConfig.getSync().isEnabled()) {
            return;
        }

        log.info("=== Starting scheduled video sync ===");

        try {
            // 최신 10개 영상 동기화
            int syncedCount = executeWithRetry(() -> youtubeSyncService.syncLatestVideos(10));

            if (syncedCount > 0) {
                log.info("✅ Video sync completed: {} new sermons added", syncedCount);
            } else {
                log.info("No new videos to sync");
            }

        } catch (Exception e) {
            log.error("❌ Failed to sync videos: {}", e.getMessage());
            // 실패필도 다음 스케줄 실행은 계속됨
        }
    }

    /**
     * Task 2: 재생목록 동기화
     * 지정된 재생목록의 영상을 VideoGallery에 동기화
     *
     * 실행 주기: 6시간마다 (cron = "0 0 *&#47;6 * * *") - 00:00, 06:00, 12:00, 18:00
     * Quota Cost: ~1 unit/call (playlistItems.list)
     * Daily Cost: 4 calls x 1 unit = 4 units
     */
    @Scheduled(cron = "${youtube.sync.playlist-sync-cron:0 0 */6 * * *}")
    public void syncPlaylists() {
        // Sync 비활성화 체크
        if (!youTubeConfig.getSync().isEnabled()) {
            return;
        }

        log.info("=== Starting scheduled playlist sync ===");

        try {
            // TODO: 실제 운영에서는 데이터베이스의 youtube_playlist 테이블에서
            //       활성화된 재생목록을 조회하여 동기화
            log.info("Playlist sync completed (no active playlists configured)");

        } catch (Exception e) {
            log.error("❌ Failed to sync playlists: {}", e.getMessage());
        }
    }

    /**
     * Task 3: 비활성 라이브 정리
     * 24시간 이상 LIVE 상태인데 실제로는 종료된 방송을 ENDED로 변경
     *
     * 실행 주기: 30분마다 (cron = "0 *&#47;30 * * * *")
     * Quota Cost: 0 units (DB 작업만)
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void cleanupInactiveLives() {
        // Sync 비활성화 체크
        if (!youTubeConfig.getSync().isEnabled()) {
            return;
        }

        log.debug("=== Starting cleanup of inactive live streams ===");

        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

            // 24시간 이상 LIVE 상태인 방송 조회
            List<YouTubeLive> staleLives = youtubeLiveRepository.findByStatus(LiveStatus.LIVE)
                .stream()
                .filter(live -> live.getActualStartTime() != null
                    && live.getActualStartTime().isBefore(cutoffTime))
                .toList();

            if (staleLives.isEmpty()) {
                log.debug("No stale live streams to cleanup");
                return;
            }

            // ENDED 상태로 변경
            int cleanedCount = 0;
            for (YouTubeLive live : staleLives) {
                live.setStatus(LiveStatus.ENDED);
                live.setEndTime(LocalDateTime.now());
                youtubeLiveRepository.save(live);
                cleanedCount++;

                log.info("Cleaned up stale live: {} (started at: {})",
                    live.getTitle(), live.getActualStartTime());
            }

            log.info("✅ Cleanup completed: {} stale live streams marked as ended", cleanedCount);

        } catch (Exception e) {
            log.error("❌ Failed to cleanup inactive lives: {}", e.getMessage());
        }
    }

    // ===== Helper Methods =====

    /**
     * 재시도 로직이 포함된 작업 실행
     * 
     * 개선사항 (2026-02-11):
     * - 401/403 오류: 재시도하지 않음 (인증/권한 오류는 재시도해도 해결되지 않음)
     * - 404 오류: 재시도하지 않음 (리소스 없음)
     * - 429/5xx/타임아웃: 재시도
     */
    private int executeWithRetry(ScheduledTask task) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRY_ATTEMPTS) {
            try {
                return task.execute();

            } catch (YouTubeApiException e) {
                // YouTube API 예외 처리
                int statusCode = e.getStatusCode();
                
                // 재시도하지 않을 오류 (400, 401, 403, 404)
                if (statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 404) {
                    log.error("Non-retryable error ({}): {}", statusCode, e.getMessage());
                    return 0; // 빈 결과 반환
                }
                
                // 재시도 가능한 오류 (429, 5xx, 기타)
                attempt++;
                lastException = e;
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.warn("Attempt {}/{} failed ({}), retrying in 5 seconds...",
                        attempt, MAX_RETRY_ATTEMPTS, statusCode);
                    sleepBeforeRetry();
                }
                
            } catch (HttpClientErrorException e) {
                // Spring HTTP Client 예외 처리
                int statusCode = e.getStatusCode().value();
                
                // 재시도하지 않을 오류
                if (statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 404) {
                    log.error("Non-retryable HTTP error ({}): {}", statusCode, e.getMessage());
                    return 0;
                }
                
                attempt++;
                lastException = e;
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.warn("Attempt {}/{} failed (HTTP {}), retrying in 5 seconds...",
                        attempt, MAX_RETRY_ATTEMPTS, statusCode);
                    sleepBeforeRetry();
                }
                
            } catch (Exception e) {
                // 기타 예외
                attempt++;
                lastException = e;

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.warn("Attempt {}/{} failed, retrying in 5 seconds...",
                        attempt, MAX_RETRY_ATTEMPTS, e);
                    sleepBeforeRetry();
                }
            }
        }

        // 모든 재시도 실패 - 로깅만 하고 0 반환 (예외를 던지지 않음)
        log.error("Task failed after {} attempts: {}", MAX_RETRY_ATTEMPTS, 
            lastException != null ? lastException.getMessage() : "Unknown error");
        return 0;
    }
    
    /**
     * 재시도 전 대기
     */
    private void sleepBeforeRetry() {
        try {
            Thread.sleep(5000); // 5초 대기
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Retry sleep interrupted");
        }
    }

    /**
     * 스케줄 작업 함수형 인터페이스
     */
    @FunctionalInterface
    private interface ScheduledTask {
        int execute() throws Exception;
    }

    /**
     * 스케줄러 상태 로깅 (초기화 시)
     */
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void logSchedulerStatus() {
        if (!youTubeConfig.getSync().isEnabled()) {
            log.warn("⚠️ YouTube Scheduler is DISABLED (youtube.sync.enabled=false)");
            return;
        }

        log.info("=============================================================");
        log.info("YouTube Scheduler Status: ACTIVE (Resilience Mode)");
        log.info("=============================================================");
        log.info("Note: Live Stream Scan is handled by SmartYouTubeScheduler");
        log.info("");
        log.info("Task 1: Video Sync");
        log.info("  - Schedule: {} (매시간)", youTubeConfig.getSync().getVideoSyncCron());
        log.info("  - Quota Cost: ~100 units/call");
        log.info("");
        log.info("Task 2: Playlist Sync");
        log.info("  - Schedule: {} (6시간마다)", youTubeConfig.getSync().getPlaylistSyncCron());
        log.info("  - Quota Cost: ~1 unit/call");
        log.info("");
        log.info("Task 3: Inactive Live Cleanup");
        log.info("  - Schedule: 0 */30 * * * * (30분마다)");
        log.info("  - Quota Cost: 0 units (DB only)");
        log.info("=============================================================");
        log.info("Resilience Features:");
        log.info("  - API Key invalid: Graceful degradation (empty results)");
        log.info("  - API Key caching: 1 hour");
        log.info("  - All exceptions caught: Service continues");
        log.info("=============================================================");
    }
}
