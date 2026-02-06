package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.YouTubePlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * YouTubePlaylist Repository
 * Context7 네이밍 규칙 적용
 */
@Repository
public interface YouTubePlaylistRepository extends JpaRepository<YouTubePlaylist, Long> {

    /**
     * 재생목록 ID로 조회
     */
    Optional<YouTubePlaylist> findByPlaylistId(String playlistId);

    /**
     * 활성화된 재생목록 조회 (표시 순서대로)
     */
    List<YouTubePlaylist> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * 활성화된 재생목록 조회 (생성일 순)
     */
    List<YouTubePlaylist> findByIsActiveTrueOrderByCreatedAtDesc();

    /**
     * 자동 동기화 활성화된 재생목록 조회
     */
    List<YouTubePlaylist> findByAutoSyncTrueAndIsActiveTrue();

    /**
     * 카테고리별 재생목록 조회
     */
    List<YouTubePlaylist> findByCategoryAndIsActiveTrue(String category);

    /**
     * 마지막 동기화 시간 이전 재생목록 조회
     */
    List<YouTubePlaylist> findByLastSyncedAtBefore(LocalDateTime dateTime);

    /**
     * 재생목록 존재 여부 확인
     */
    boolean existsByPlaylistId(String playlistId);

    /**
     * 동기화가 필요한 재생목록 조회 (lastSyncedAt이 null이거나 특정 시간 이전)
     */
    List<YouTubePlaylist> findByIsActiveTrueAndLastSyncedAtIsNull();
}
