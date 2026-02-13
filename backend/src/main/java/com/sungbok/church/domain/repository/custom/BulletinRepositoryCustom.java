package com.sungbok.church.domain.repository.custom;

/**
 * Bulletin Repository Custom Interface
 * QueryDSL을 사용한 복잡한 쿼리 메서드 정의
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public interface BulletinRepositoryCustom {

    /**
     * 다운로드 수 증가
     *
     * @param id 주보 ID
     * @return 업데이트된 행 수
     */
    long incrementDownloadCount(Long id);
}
