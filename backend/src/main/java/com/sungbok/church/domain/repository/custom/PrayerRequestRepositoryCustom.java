package com.sungbok.church.domain.repository.custom;

/**
 * PrayerRequest Repository Custom Interface
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface PrayerRequestRepositoryCustom {

    /**
     * 기도수 증가
     *
     * @param id 기도요청 ID
     * @return 업데이트된 행 수
     */
    long incrementPrayerCount(Long id);
}
