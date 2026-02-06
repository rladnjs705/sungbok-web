package com.sungbok.church.service;

import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * YouTubeLive Service
 * 유튜브 라이브 방송 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YouTubeLiveService {

    private final YouTubeLiveRepository youtubeLiveRepository;

    /**
     * 라이브 상태별 조회
     */
    public List<YouTubeLive> getLivesByStatus(LiveStatus status) {
        return youtubeLiveRepository.findByStatus(status);
    }

    /**
     * 현재 라이브 중인 방송 조회
     */
    public List<YouTubeLive> getCurrentLiveStreams() {
        return youtubeLiveRepository.findByStatusOrderByActualStartTimeDesc(LiveStatus.LIVE);
    }

    /**
     * 예정된 라이브 조회
     */
    public List<YouTubeLive> getUpcomingLiveStreams() {
        return youtubeLiveRepository.findByStatusOrderByScheduledStartTimeAsc(LiveStatus.UPCOMING);
    }

    /**
     * YouTube Video ID로 조회
     */
    public Optional<YouTubeLive> getLiveByVideoId(String youtubeVideoId) {
        return youtubeLiveRepository.findByYoutubeVideoId(youtubeVideoId);
    }

    /**
     * 종료된 라이브 조회 (특정 기간)
     */
    public List<YouTubeLive> getCompletedLivesSince(LocalDateTime since) {
        return youtubeLiveRepository.findByStatusAndEndTimeAfter(LiveStatus.COMPLETED, since);
    }

    /**
     * 최신 라이브 N개 조회
     */
    public List<YouTubeLive> getLatestLives() {
        return youtubeLiveRepository.findTop10ByOrderByCreatedAtDesc();
    }

    /**
     * 라이브 생성 (YouTube API 연동)
     */
    @Transactional
    public YouTubeLive createLive(YouTubeLive live) {
        validateVideoIdUniqueness(live.getYoutubeVideoId());
        return youtubeLiveRepository.save(live);
    }

    /**
     * 라이브 상태 업데이트
     */
    @Transactional
    public YouTubeLive updateLiveStatus(Long id, LiveStatus status) {
        YouTubeLive live = youtubeLiveRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("라이브를 찾을 수 없습니다: " + id));

        live.setStatus(status);

        // 상태에 따른 시간 업데이트
        if (status == LiveStatus.LIVE && live.getActualStartTime() == null) {
            live.setActualStartTime(LocalDateTime.now());
        } else if (status == LiveStatus.COMPLETED && live.getEndTime() == null) {
            live.setEndTime(LocalDateTime.now());
        }

        return youtubeLiveRepository.save(live);
    }

    /**
     * 라이브 정보 수정
     */
    @Transactional
    public YouTubeLive updateLive(Long id, YouTubeLive updatedLive) {
        YouTubeLive live = youtubeLiveRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("라이브를 찾을 수 없습니다: " + id));

        live.setTitle(updatedLive.getTitle());
        live.setDescription(updatedLive.getDescription());
        live.setScheduledStartTime(updatedLive.getScheduledStartTime());
        live.setThumbnailUrl(updatedLive.getThumbnailUrl());

        return youtubeLiveRepository.save(live);
    }

    /**
     * 라이브 삭제
     */
    @Transactional
    public void deleteLive(Long id) {
        if (!youtubeLiveRepository.existsById(id)) {
            throw new IllegalArgumentException("라이브를 찾을 수 없습니다: " + id);
        }
        youtubeLiveRepository.deleteById(id);
    }

    /**
     * 오래된 라이브 데이터 정리 (자동 스케줄링용)
     */
    @Transactional
    public void cleanupOldLives(LocalDateTime before) {
        List<YouTubeLive> oldLives = youtubeLiveRepository
            .findByStatusAndEndTimeBefore(LiveStatus.COMPLETED, before);

        youtubeLiveRepository.deleteAll(oldLives);
    }

    /**
     * Video ID 중복 체크
     */
    private void validateVideoIdUniqueness(String youtubeVideoId) {
        if (youtubeLiveRepository.existsByYoutubeVideoId(youtubeVideoId)) {
            throw new IllegalArgumentException("이미 존재하는 Video ID입니다: " + youtubeVideoId);
        }
    }
}
