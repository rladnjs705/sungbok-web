package com.sungbok.church.domain.repository.custom;

import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.dto.projection.SermonProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * SermonRepository QueryDSL Custom Interface
 *
 * Context7 Best Practices:
 * - Custom Repository 패턴으로 QueryDSL 쿼리 분리
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - Native Query 제거로 LazyInitializationException 방지
 */
public interface SermonRepositoryCustom {

    /**
     * 공개된 설교 목록 조회 (QueryDSL)
     *
     * @param pageable 페이징 정보
     * @return 공개된 설교 목록 (Projection DTO)
     */
    Page<SermonProjectionDto> findPublishedSermons(Pageable pageable);

    /**
     * 설교자별 설교 조회 (QueryDSL)
     *
     * @param preacher 설교자 이름
     * @param pageable 페이징 정보
     * @return 설교자별 설교 목록 (Projection DTO)
     */
    Page<SermonProjectionDto> findByPreacher(String preacher, Pageable pageable);

    /**
     * 날짜 범위로 설교 조회 (QueryDSL)
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @param pageable  페이징 정보
     * @return 날짜 범위 내 설교 목록 (Projection DTO)
     */
    Page<SermonProjectionDto> findByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * 추천 설교 목록 (QueryDSL)
     *
     * @return 추천 설교 목록 (Projection DTO)
     */
    List<SermonProjectionDto> findFeatured();

    /**
     * 최신 설교 목록 (QueryDSL)
     *
     * @return 최신 설교 목록 (Projection DTO, 최대 10개)
     */
    List<SermonProjectionDto> findLatest();

    /**
     * 예배 유형별 최신 설교 조회 (QueryDSL)
     *
     * @param worshipType 예배 유형
     * @param limit       조회할 설교 개수
     * @return 예배 유형별 최신 설교 목록 (Projection DTO)
     */
    List<SermonProjectionDto> findByWorshipType(WorshipType worshipType, int limit);
}
