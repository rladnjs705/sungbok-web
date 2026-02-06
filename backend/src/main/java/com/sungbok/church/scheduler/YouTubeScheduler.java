package com.sungbok.church.scheduler;

import com.sungbok.church.config.YouTubeConfig;
import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import com.sungbok.church.service.YouTubeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * YouTube Scheduler
 * YouTube API 자동 동기화 스케줄러
 *
 * 4가지 주요 작업:
 * 1. 라이브 스캔 (1분마다)
 * 2. 영상 동기화 (매시간)
 * 3. 재생목록 동기화 (6시간마다)
 * 4. 비활성 라이브 정리 (30분마다)
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
     * Task 1: 라이브 방송 스캔
     * 예배 시간에 자동으로 라이브 방송을 감지하고 상태를 업데이트
     *
     * 실행 주기: 1분마다 (fixedRate = 60000ms)
     * Quota Cost: ~100 units/call (search.list)
     * Daily Cost: 1,440 calls × 100 units = 144,000 units ⚠️ (Phase 4에서 최적화 필요)
     */
    @Scheduled(fixedRateString = "${youtube.sync.live-check-interval:60000}")
    public void scanLiveStreams() {
        // Sync 비활성화 체크
        if (!youTubeConfig.getSync().isEnabled()) {
            return;
        }

        log.info("=== Starting scheduled live stream scan ===");

        try {
            int updatedCount = executeWithRetry(() -> youtubeSyncService.updateLiveStatus());

            if (updatedCount > 0) {
                log.info("✅ Live scan completed: {} live streams updated", updatedCount);
            } else {
                log.debug("No live stream updates");
            }

        } catch (Exception e) {
            log.error("❌ Failed to scan live streams after {} retries", MAX_RETRY_ATTEMPTS, e);
            // 실패해도 다음 스케줄 실행은 계속됨
        }
    }

    /**
     * Task 2: 최신 영상 동기화
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
            log.error("❌ Failed to sync videos after {} retries", MAX_RETRY_ATTEMPTS, e);
        }
    }

    /**
     * Task 3: 재생목록 동기화
     * 지정된 재생목록의 영상을 VideoGallery에 동기화
     *
     * 실행 주기: 6시간마다 (cron = "0 0 *&#47;6 * * *") - 00:00, 06:00, 12:00, 18:00
     * Quota Cost: ~1 unit/call (playlistItems.list)
     * Daily Cost: 4 calls x 1 unit = 4 units
     *
     * TODO: 재생목록 ID는 설정 파일 또는 데이터베이스에서 관리
     */
    @Scheduled(cron = "${youtube.sync.playlist-sync-cron:0 0 */6 * * *}")
    public void syncPlaylists() {
        // Sync 비활성화 체크
        if (!youTubeConfig.getSync().isEnabled()) {
            return;
        }

        log.info("=== Starting scheduled playlist sync ===");

        // TODO: 실제 운영에서는 데이터베이스의 youtube_playlist 테이블에서
        //       활성화된 재생목록을 조회하여 동기화
        //       현재는 예시로 비활성화

        try {
            // Example: 특정 재생목록 동기화
            // List<YouTubePlaylist> activePlaylists = youtubePlaylistRepository.findByIsActiveTrue();
            // for (YouTubePlaylist playlist : activePlaylists) {
            //     int syncedCount = youtubeSyncService.syncPlaylist(
            //         playlist.getPlaylistId(),
            //         playlist.getCategory()
            //     );
            //     log.info("Synced playlist '{}': {} videos", playlist.getTitle(), syncedCount);
            // }

            log.info("Playlist sync completed (no active playlists configured)");

        } catch (Exception e) {
            log.error("❌ Failed to sync playlists", e);
        }
    }

    /**
     * Task 4: 비활성 라이브 정리
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

        log.info("=== Starting cleanup of inactive live streams ===");

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
            log.error("❌ Failed to cleanup inactive lives", e);
        }
    }

    // ===== Helper Methods =====

    /**
     * 재시도 로직이 포함된 작업 실행
     * 최대 3번 재시도, 실패 시 예외 발생
     */
    private int executeWithRetry(ScheduledTask task) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRY_ATTEMPTS) {
            try {
                return task.execute();

            } catch (Exception e) {
                attempt++;
                lastException = e;

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    log.warn("Attempt {}/{} failed, retrying in 5 seconds...",
                        attempt, MAX_RETRY_ATTEMPTS, e);

                    try {
                        Thread.sleep(5000); // 5초 대기
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        // 모든 재시도 실패
        throw new RuntimeException("Task failed after " + MAX_RETRY_ATTEMPTS + " attempts",
            lastException);
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
        log.info("YouTube Scheduler Status: ACTIVE");
        log.info("=============================================================");
        log.info("Task 1: Live Stream Scan");
        log.info("  - Interval: Every {} ms ({}분마다)",
            youTubeConfig.getSync().getLiveCheckInterval(),
            youTubeConfig.getSync().getLiveCheckInterval() / 60000);
        log.info("  - Quota Cost: ~100 units/call");
        log.info("");
        log.info("Task 2: Video Sync");
        log.info("  - Schedule: {} (매시간)", youTubeConfig.getSync().getVideoSyncCron());
        log.info("  - Quota Cost: ~100 units/call");
        log.info("");
        log.info("Task 3: Playlist Sync");
        log.info("  - Schedule: {} (6시간마다)", youTubeConfig.getSync().getPlaylistSyncCron());
        log.info("  - Quota Cost: ~1 unit/call");
        log.info("");
        log.info("Task 4: Inactive Live Cleanup");
        log.info("  - Schedule: 0 */30 * * * * (30분마다)");
        log.info("  - Quota Cost: 0 units (DB only)");
        log.info("=============================================================");
        log.info("⚠️ IMPORTANT: Phase 4에서 Quota 최적화 필요!");
        log.info("  - Current estimated daily cost: ~147,000 units");
        log.info("  - Daily limit: 10,000 units");
        log.info("  - Solution: Caching + Rate limiting 구현 예정");
        log.info("=============================================================");
    }
}
