package com.sungbok.church.controller;

import com.sungbok.church.util.YouTubeQuotaTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * YouTube Quota Monitoring Controller
 * API 할당량 모니터링 및 관리 엔드포인트
 *
 * 주요 기능:
 * 1. 현재 할당량 사용량 조회
 * 2. 할당량 상태 대시보드
 * 3. 캐시 통계 조회
 * 4. 수동 리셋 (테스트용)
 */
@RestController
@RequestMapping("/api/admin/youtube-quota")
@RequiredArgsConstructor
@Slf4j
public class YouTubeQuotaController {

    private final YouTubeQuotaTracker quotaTracker;
    private final CacheManager cacheManager;

    /**
     * 할당량 상태 대시보드
     */
    @GetMapping("/status")
    public ResponseEntity<YouTubeQuotaTracker.QuotaStatus> getQuotaStatus() {
        YouTubeQuotaTracker.QuotaStatus status = quotaTracker.getQuotaStatus();
        log.info("Quota status requested: {}/{} units ({}%)",
            status.currentUsage(), status.dailyLimit(),
            String.format("%.2f", status.usagePercentage()));
        return ResponseEntity.ok(status);
    }

    /**
     * 할당량 상세 정보
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getQuotaDetails() {
        YouTubeQuotaTracker.QuotaStatus status = quotaTracker.getQuotaStatus();

        Map<String, Object> details = new HashMap<>();
        details.put("currentUsage", status.currentUsage());
        details.put("remainingQuota", status.remainingQuota());
        details.put("dailyLimit", status.dailyLimit());
        details.put("usagePercentage", String.format("%.2f%%", status.usagePercentage()));
        details.put("status", status.status());
        details.put("timestamp", status.timestamp());

        // 상태별 색상 코드
        details.put("color", switch (status.status()) {
            case "OK" -> "green";
            case "WARNING" -> "yellow";
            case "CRITICAL" -> "orange";
            case "EXHAUSTED" -> "red";
            default -> "gray";
        });

        // 예상 리셋 시간 (다음날 00:00 KST)
        details.put("resetTime", "매일 00:00 (KST) 자동 리셋");

        return ResponseEntity.ok(details);
    }

    /**
     * 할당량 예측 (현재 사용률 기반)
     */
    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getForecast() {
        YouTubeQuotaTracker.QuotaStatus status = quotaTracker.getQuotaStatus();

        // 현재 시간 기준으로 하루 종료까지 남은 시간 계산
        long currentHour = java.time.LocalDateTime.now().getHour();
        long hoursRemaining = 24 - currentHour;

        // 시간당 평균 사용량
        double hourlyAverage = currentHour > 0 ? (double) status.currentUsage() / currentHour : 0;

        // 예상 총 사용량
        int projectedTotal = (int) (status.currentUsage() + (hourlyAverage * hoursRemaining));

        Map<String, Object> forecast = new HashMap<>();
        forecast.put("currentUsage", status.currentUsage());
        forecast.put("hourlyAverage", String.format("%.2f units/hour", hourlyAverage));
        forecast.put("hoursRemaining", hoursRemaining);
        forecast.put("projectedTotal", projectedTotal);
        forecast.put("projectedPercentage", String.format("%.2f%%",
            (double) projectedTotal / status.dailyLimit() * 100));
        forecast.put("willExceedLimit", projectedTotal > status.dailyLimit());

        if (projectedTotal > status.dailyLimit()) {
            forecast.put("warning", "⚠️ 현재 사용률로는 일일 할당량을 초과할 것으로 예상됩니다!");
        } else {
            forecast.put("message", "✅ 현재 사용률로는 할당량 내에서 운영 가능합니다.");
        }

        return ResponseEntity.ok(forecast);
    }

    /**
     * 캐시 통계 조회
     */
    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // TODO: Valkey 캐시 통계 구현 필요
            // Caffeine에서 Valkey로 변경됨
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    Map<String, Object> cacheInfo = new HashMap<>();
                    cacheInfo.put("name", cacheName);
                    cacheInfo.put("type", "valkey");
                    stats.put(cacheName, cacheInfo);
                }
            }

            log.info("Cache stats requested: {}", stats);

        } catch (Exception e) {
            log.error("Error getting cache stats", e);
            stats.put("error", "Failed to retrieve cache stats: " + e.getMessage());
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * 할당량 수동 리셋 (테스트용)
     * ⚠️ 프로덕션에서는 비활성화 필요
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetQuota() {
        log.warn("⚠️ Manual quota reset requested!");

        int previousUsage = quotaTracker.getCurrentUsage();
        quotaTracker.resetQuota();
        int currentUsage = quotaTracker.getCurrentUsage();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Quota reset successfully");
        response.put("previousUsage", previousUsage);
        response.put("currentUsage", currentUsage);
        response.put("warning", "This is a test feature. In production, quota resets automatically at midnight.");

        return ResponseEntity.ok(response);
    }

    /**
     * 캐시 수동 초기화 (테스트용)
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, Object>> clearCache(
            @RequestParam(required = false) String cacheName) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (cacheName != null && !cacheName.isEmpty()) {
                // 특정 캐시만 초기화
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    response.put("message", "Cache '" + cacheName + "' cleared successfully");
                    log.info("Cache cleared: {}", cacheName);
                } else {
                    response.put("error", "Cache not found: " + cacheName);
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                // 모든 캐시 초기화
                for (String name : cacheManager.getCacheNames()) {
                    var cache = cacheManager.getCache(name);
                    if (cache != null) {
                        cache.clear();
                    }
                }
                response.put("message", "All caches cleared successfully");
                log.info("All caches cleared");
            }

            response.put("success", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error clearing cache", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Health Check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("YouTube Quota Monitor is running");
    }
}
