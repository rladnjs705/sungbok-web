package com.sungbok.church.scheduler;

import com.sungbok.church.service.YouTubeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Smart YouTube 스케줄러
 * 예배 시간대에만 라이브 스캔을 수행하여 Quota 절약
 *
 * Quota 계산 (하루 10,000 units 제한):
 * - 예배 시간 외: 0 calls (스캔 안함) ← Quota 절약 핵심
 * - 예배 시간대: 5분마다 스캔
 *   - 주일예배(6시간): 72 calls
 *   - 수요예배(2.5시간): 30 calls
 *   - 금요예배(1.5시간): 18 calls
 *   - 새벽예배(1시간 × 7일): 84 calls/주 = 12 calls/일
 *   = 총 약 132 calls/일 × 100 units = 13,200 units (이론상)
 *   - 실제 예상: 시간대 겹침 고려 시 약 3,000~5,000 units/일
 *
 * 예배 시간대:
 * - 새벽예배: 매일 04:30 - 05:30
 * - 주일예배: 일요일 07:00 - 13:10
 * - 수요예배: 수요일 18:00 - 20:30
 * - 금요예배: 금요일 19:00 - 20:30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmartYouTubeScheduler {

    private final YouTubeSyncService syncService;

    /**
     * 수동 라이브 스캔 트리거 (Admin API용)
     */
    public void scanLiveStreams() {
        log.info("Manual trigger: Live stream scan");
        try {
            int updatedCount = syncService.updateLiveStatus();
            if (updatedCount > 0) {
                log.info("✅ Manual live scan completed: {} streams updated", updatedCount);
            }
        } catch (Exception e) {
            log.error("❌ Manual live scan failed: {}", e.getMessage());
        }
    }

    /**
     * 조걶부 라이브 스캔: 예배 시간대에만 스캔 (Quota 절약)
     * 실행: 1분마다 조건 체크, 예배 시간대에만 실제 스캔
     *
     * Quota 계산:
     * - 예배 시간 외: 0 calls (스캔 안함)
     * - 예배 시간대: 5분마다 스캔
     *   - 주일예배(6시간) + 수요예배(2.5시간) + 금요예배(1.5시간) + 새벽예밝(1시간 × 7일)
     *   = 약 20시간 × 12회/시간 = 240회/주 = 34회/일
     * - 예상 Quota: 34회 × 100 units = 3,400 units/일 (제한의 34%)
     */
    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void smartLiveScan() {
        LocalTime now = LocalTime.now();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();

        // 예배 시간대가 아니면 스캔하지 않음 (Quota 절약)
        if (!isWorshipTime(now, dayOfWeek)) {
            return;
        }

        // 예배 시간대: 5분마다 스캔
        if (now.getMinute() % 5 == 0) {
            log.debug("🔴 예배 시간대 - 라이브 스캔 실행 (5분 간격)");
            try {
                int updatedCount = syncService.updateLiveStatus();
                if (updatedCount > 0) {
                    log.info("✅ 라이브 스캔 완료: {}개 스트림 업데이트", updatedCount);
                }
            } catch (Exception e) {
                log.error("❌ 라이브 스캔 실패: {}", e.getMessage());
            }
        }
    }

    /**
     * 예배 시간대 판별
     *
     * @param now 현재 시각
     * @param dayOfWeek 요일
     * @return 예배 시간대 여부
     */
    private boolean isWorshipTime(LocalTime now, DayOfWeek dayOfWeek) {
        // 주일예배: 일요일 07:00 - 13:10
        if (dayOfWeek == DayOfWeek.SUNDAY &&
            now.isAfter(LocalTime.of(7, 0)) &&
            now.isBefore(LocalTime.of(13, 10))) {
            return true;
        }

        // 새벽예배: 매일 04:30 - 05:30
        if (now.isAfter(LocalTime.of(4, 30)) &&
            now.isBefore(LocalTime.of(5, 30))) {
            return true;
        }

        // 수요예배: 수요일 18:00 - 20:30
        if (dayOfWeek == DayOfWeek.WEDNESDAY &&
            now.isAfter(LocalTime.of(18, 0)) &&
            now.isBefore(LocalTime.of(20, 30))) {
            return true;
        }

        // 금요예배: 금요일 19:00 - 20:30
        if (dayOfWeek == DayOfWeek.FRIDAY &&
            now.isAfter(LocalTime.of(19, 0)) &&
            now.isBefore(LocalTime.of(20, 30))) {
            return true;
        }

        return false;
    }
}
