package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * YouTubeLive Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface YouTubeLiveRepository extends JpaRepository<YouTubeLive, Long> {

    /**
     * ID로 조회 (Worship 포함)
     * N+1 쿼리 방지를 위해 Worship을 함께 로드
     */
    @EntityGraph(attributePaths = {"worship"})
    @Override
    Optional<YouTubeLive> findById(Long id);

    /**
     * 라이브 상태별 조회
     */
    List<YouTubeLive> findByStatus(LiveStatus status);

    /**
     * 현재 라이브 중인 방송 조회
     */
    List<YouTubeLive> findByStatusOrderByActualStartTimeDesc(LiveStatus status);

    /**
     * YouTube Video ID로 조회
     */
    Optional<YouTubeLive> findByYoutubeVideoId(String youtubeVideoId);

    /**
     * 예정된 라이브 조회 (시작 시간 순)
     */
    List<YouTubeLive> findByStatusOrderByScheduledStartTimeAsc(LiveStatus status);

    /**
     * 종료된 라이브 조회 (특정 기간)
     */
    List<YouTubeLive> findByStatusAndEndTimeAfter(LiveStatus status, LocalDateTime after);

    /**
     * 최신 라이브 N개 조회
     */
    List<YouTubeLive> findTop10ByOrderByCreatedAtDesc();

    /**
     * 라이브 존재 여부 확인
     */
    boolean existsByYoutubeVideoId(String youtubeVideoId);

    /**
     * 오래된 라이브 데이터 삭제용 조회
     */
    List<YouTubeLive> findByStatusAndEndTimeBefore(LiveStatus status, LocalDateTime before);
}
