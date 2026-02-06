package com.sungbok.church.util;

import com.sungbok.church.config.YouTubeConfig;
import com.sungbok.church.exception.YouTubeApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * YouTube API Quota Tracker
 * YouTube Data API v3 일일 할당량(10,000 units) 추적 및 관리
 *
 * Quota Costs:
 * - search.list: 100 units
 * - videos.list: 1 unit
 * - channels.list: 1 unit
 * - playlistItems.list: 1 unit
 *
 * Circuit Breaker:
 * - 80% (8,000 units) 도달 시 경고
 * - 90% (9,000 units) 도달 시 비필수 API 호출 차단
 * - 100% (10,000 units) 도달 시 모든 API 호출 차단
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class YouTubeQuotaTracker {

    private final YouTubeConfig youTubeConfig;

    // 날짜별 사용량 추적 (날짜가 바뀌면 자동 리셋)
    private final ConcurrentHashMap<LocalDate, AtomicInteger> dailyUsage = new ConcurrentHashMap<>();

    // API 호출 타입별 비용
    public static final int SEARCH_COST = 100;
    public static final int VIDEO_COST = 1;
    public static final int CHANNEL_COST = 1;
    public static final int PLAYLIST_COST = 1;

    /**
     * API 호출 전 할당량 체크
     * 할당량 초과 시 예외 발생
     *
     * @param cost API 호출 비용 (units)
     * @throws YouTubeApiException 할당량 초과 시
     */
    public void checkQuota(int cost) {
        int currentUsage = getCurrentUsage();
        int dailyLimit = youTubeConfig.getQuota().getDailyLimit();

        if (currentUsage + cost > dailyLimit) {
            log.error("❌ Quota exceeded! Current: {}, Required: {}, Limit: {}",
                currentUsage, cost, dailyLimit);
            throw YouTubeApiException.quotaExceeded(
                String.format("Daily quota exceeded: %d/%d units used", currentUsage, dailyLimit)
            );
        }

        // 90% 경고
        if (currentUsage + cost > dailyLimit * 0.9) {
            log.warn("⚠️ Quota critical: {}/{} units (>90%)", currentUsage + cost, dailyLimit);
        }
        // 80% 경고
        else if (currentUsage + cost > youTubeConfig.getQuota().getWarningThreshold()) {
            log.warn("⚠️ Quota warning: {}/{} units (>80%)", currentUsage + cost, dailyLimit);
        }
    }

    /**
     * API 호출 후 사용량 기록
     *
     * @param cost API 호출 비용 (units)
     */
    public void recordUsage(int cost) {
        LocalDate today = LocalDate.now();
        AtomicInteger usage = dailyUsage.computeIfAbsent(today, k -> new AtomicInteger(0));
        int newUsage = usage.addAndGet(cost);

        log.debug("Quota used: {} units (Total today: {})", cost, newUsage);

        // 오래된 날짜 데이터 정리 (메모리 절약)
        cleanupOldData();
    }

    /**
     * 현재 일일 사용량 조회
     *
     * @return 오늘 사용한 quota (units)
     */
    public int getCurrentUsage() {
        LocalDate today = LocalDate.now();
        AtomicInteger usage = dailyUsage.get(today);
        return usage != null ? usage.get() : 0;
    }

    /**
     * 남은 할당량 조회
     *
     * @return 남은 quota (units)
     */
    public int getRemainingQuota() {
        int dailyLimit = youTubeConfig.getQuota().getDailyLimit();
        return Math.max(0, dailyLimit - getCurrentUsage());
    }

    /**
     * 할당량 사용률 (%)
     *
     * @return 0-100 사이의 백분율
     */
    public double getUsagePercentage() {
        int dailyLimit = youTubeConfig.getQuota().getDailyLimit();
        return (double) getCurrentUsage() / dailyLimit * 100;
    }

    /**
     * API 호출 가능 여부 체크 (Circuit Breaker)
     *
     * @param cost 필요한 quota
     * @param isEssential 필수 API 호출 여부 (true: 라이브 스캔, false: 일반 동기화)
     * @return true: 호출 가능, false: 호출 불가
     */
    public boolean canMakeApiCall(int cost, boolean isEssential) {
        int currentUsage = getCurrentUsage();
        int dailyLimit = youTubeConfig.getQuota().getDailyLimit();

        // 100% 도달 시 모든 호출 차단
        if (currentUsage + cost > dailyLimit) {
            log.warn("🚫 API call blocked: Quota exhausted ({}/{})", currentUsage, dailyLimit);
            return false;
        }

        // 90% 도달 시 비필수 호출만 차단
        if (currentUsage + cost > dailyLimit * 0.9 && !isEssential) {
            log.warn("🚫 Non-essential API call blocked: Quota critical ({}/{})",
                currentUsage, dailyLimit);
            return false;
        }

        return true;
    }

    /**
     * 할당량 수동 리셋 (테스트용)
     */
    public void resetQuota() {
        LocalDate today = LocalDate.now();
        dailyUsage.put(today, new AtomicInteger(0));
        log.info("Quota reset manually for {}", today);
    }

    /**
     * 할당량 상태 정보
     */
    @Getter
    public static class QuotaStatus {
        private final int currentUsage;
        private final int remainingQuota;
        private final int dailyLimit;
        private final double usagePercentage;
        private final String status; // OK, WARNING, CRITICAL, EXHAUSTED
        private final LocalDateTime timestamp;

        public QuotaStatus(int currentUsage, int remainingQuota, int dailyLimit,
                          double usagePercentage, String status) {
            this.currentUsage = currentUsage;
            this.remainingQuota = remainingQuota;
            this.dailyLimit = dailyLimit;
            this.usagePercentage = usagePercentage;
            this.status = status;
            this.timestamp = LocalDateTime.now();
        }
    }

    /**
     * 현재 할당량 상태 조회
     */
    public QuotaStatus getQuotaStatus() {
        int currentUsage = getCurrentUsage();
        int remainingQuota = getRemainingQuota();
        int dailyLimit = youTubeConfig.getQuota().getDailyLimit();
        double usagePercentage = getUsagePercentage();

        String status;
        if (usagePercentage >= 100) {
            status = "EXHAUSTED";
        } else if (usagePercentage >= 90) {
            status = "CRITICAL";
        } else if (usagePercentage >= 80) {
            status = "WARNING";
        } else {
            status = "OK";
        }

        return new QuotaStatus(currentUsage, remainingQuota, dailyLimit, usagePercentage, status);
    }

    /**
     * 오래된 날짜 데이터 정리 (2일 이상 지난 데이터 삭제)
     */
    private void cleanupOldData() {
        LocalDate cutoffDate = LocalDate.now().minusDays(2);
        dailyUsage.entrySet().removeIf(entry -> entry.getKey().isBefore(cutoffDate));
    }

    /**
     * API 호출 비용 계산 헬퍼
     */
    public static int calculateCost(String apiMethod, int itemCount) {
        return switch (apiMethod.toLowerCase()) {
            case "search" -> SEARCH_COST;
            case "videos" -> VIDEO_COST * Math.max(1, itemCount);
            case "channels" -> CHANNEL_COST;
            case "playlists" -> PLAYLIST_COST * Math.max(1, itemCount);
            default -> 1;
        };
    }
}
