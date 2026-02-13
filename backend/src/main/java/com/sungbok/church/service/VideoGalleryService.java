package com.sungbok.church.service;

import com.sungbok.church.domain.entity.VideoGallery;
import com.sungbok.church.domain.repository.VideoGalleryRepository;
import com.sungbok.church.dto.projection.VideoGalleryProjectionDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * VideoGallery Service
 * 동영상 갤러리 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoGalleryService {

    private final VideoGalleryRepository videoGalleryRepository;
    private final EntityManager entityManager;

    /**
     * 공개된 동영상 목록 조회 (페이징)
     */
    public Page<VideoGallery> getPublishedVideos(Pageable pageable) {
        return videoGalleryRepository.findByIsPublishedTrueOrderByEventDateDesc(pageable);
    }

    /**
     * 카테고리별 동영상 조회
     */
    public Page<VideoGallery> getVideosByCategory(String category, Pageable pageable) {
        return videoGalleryRepository.findByCategoryAndIsPublishedTrueOrderByEventDateDesc(
            category, pageable
        );
    }

    /**
     * 날짜 범위로 동영상 조회
     */
    public Page<VideoGallery> getVideosByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        return videoGalleryRepository.findByEventDateBetweenAndIsPublishedTrue(
            startDate, endDate, pageable
        );
    }

    /**
     * 동영상 ID로 조회 및 조회수 증가
     */
    @Transactional
    public VideoGallery getVideoById(Long id) {
        VideoGallery video = videoGalleryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("동영상을 찾을 수 없습니다: " + id));

        videoGalleryRepository.incrementViewCount(id);

        // 엔티티 새로고침 (업데이트된 조회수 반영)
        entityManager.refresh(video);

        return video;
    }

    /**
     * 최신 동영상 조회
     */
    public List<VideoGallery> getLatestVideos() {
        return videoGalleryRepository.findTop10ByIsPublishedTrueOrderByEventDateDesc();
    }

    /**
     * 제목으로 검색
     */
    public Page<VideoGallery> searchByTitle(String keyword, Pageable pageable) {
        return videoGalleryRepository.findByTitleContainingAndIsPublishedTrue(keyword, pageable);
    }

    /**
     * 제목 또는 설명으로 검색
     */
    public Page<VideoGalleryProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        return videoGalleryRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 동영상 생성
     */
    @Transactional
    public VideoGallery createVideo(VideoGallery video) {
        return videoGalleryRepository.save(video);
    }

    /**
     * 동영상 수정
     */
    @Transactional
    public VideoGallery updateVideo(Long id, VideoGallery updatedVideo) {
        VideoGallery video = getVideoById(id);

        video.setTitle(updatedVideo.getTitle());
        video.setDescription(updatedVideo.getDescription());
        video.setYoutubeVideoId(updatedVideo.getYoutubeVideoId());
        video.setVideoUrl(updatedVideo.getVideoUrl());
        video.setThumbnailUrl(updatedVideo.getThumbnailUrl());
        video.setEventDate(updatedVideo.getEventDate());
        video.setCategory(updatedVideo.getCategory());
        video.setIsPublished(updatedVideo.getIsPublished());

        return videoGalleryRepository.save(video);
    }

    /**
     * 동영상 삭제
     */
    @Transactional
    public void deleteVideo(Long id) {
        if (!videoGalleryRepository.existsById(id)) {
            throw new IllegalArgumentException("동영상을 찾을 수 없습니다: " + id);
        }
        videoGalleryRepository.deleteById(id);
    }

    /**
     * 통계: 카테고리별 동영상 개수
     */
    public long getVideoCountByCategory(String category) {
        return videoGalleryRepository.countByCategoryAndIsPublished(category, true);
    }
}
