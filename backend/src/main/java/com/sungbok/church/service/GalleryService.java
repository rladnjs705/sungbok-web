package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.entity.GalleryImage;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.repository.GalleryImageRepository;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.repository.GalleryRepository;
import com.sungbok.church.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Gallery Service
 * 갤러리 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final GalleryImageRepository galleryImageRepository;
    private final EntityManager entityManager;

    /**
     * 공개된 갤러리 목록 조회 (페이징)
     */
    public Page<Gallery> getPublishedGalleries(Pageable pageable) {
        return galleryRepository.findByIsPublishedTrueOrderByEventDateDesc(pageable);
    }

    /**
     * 갤러리 ID로 조회 및 조회수 증가
     */
    @Transactional
    public Gallery getGalleryById(Long id) {
        Gallery gallery = galleryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("갤러리를 찾을 수 없습니다: " + id));

        // 조회수 증가
        galleryRepository.incrementViewCount(id);

        // 엔티티 새로고침 (업데이트된 조회수 반영)
        entityManager.refresh(gallery);

        return gallery;
    }

    /**
     * 갤러리 이미지 목록 조회
     */
    public List<GalleryImage> getGalleryImages(Long galleryId) {
        return galleryImageRepository.findByGalleryIdOrderByDisplayOrderAsc(galleryId);
    }

    /**
     * 날짜 범위로 갤러리 조회
     */
    public Page<Gallery> getGalleriesByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        return galleryRepository.findByEventDateBetweenAndIsPublishedTrue(
            startDate, endDate, pageable
        );
    }

    /**
     * 최신 갤러리 N개 조회
     */
    public List<Gallery> getLatestGalleries() {
        return galleryRepository.findTop10ByIsPublishedTrueOrderByEventDateDesc();
    }

    /**
     * 제목으로 검색
     */
    public Page<Gallery> searchByTitle(String keyword, Pageable pageable) {
        return galleryRepository.findByTitleContainingAndIsPublishedTrue(keyword, pageable);
    }

    /**
     * 제목 또는 설명으로 검색
     */
    public Page<Gallery> searchByKeyword(String keyword, Pageable pageable) {
        return galleryRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 갤러리 생성
     */
    @Transactional
    public Gallery createGallery(Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    /**
     * 갤러리 이미지 추가
     */
    @Transactional
    public GalleryImage addGalleryImage(Long galleryId, GalleryImage image) {
        Gallery gallery = galleryRepository.findById(galleryId)
            .orElseThrow(() -> new ResourceNotFoundException("갤러리를 찾을 수 없습니다: " + galleryId));

        image.setGallery(gallery);
        return galleryImageRepository.save(image);
    }

    /**
     * 갤러리 수정
     */
    @Transactional
    public Gallery updateGallery(Long id, Gallery updatedGallery) {
        Gallery gallery = galleryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("갤러리를 찾을 수 없습니다: " + id));

        gallery.setTitle(updatedGallery.getTitle());
        gallery.setDescription(updatedGallery.getDescription());
        gallery.setEventDate(updatedGallery.getEventDate());
        gallery.setCoverImageUrl(updatedGallery.getCoverImageUrl());
        gallery.setIsPublished(updatedGallery.getIsPublished());

        return galleryRepository.save(gallery);
    }

    /**
     * 갤러리 삭제 (이미지 포함)
     */
    @Transactional
    public void deleteGallery(Long id) {
        if (!galleryRepository.existsById(id)) {
            throw new ResourceNotFoundException("갤러리를 찾을 수 없습니다: " + id);
        }
        galleryImageRepository.deleteByGalleryId(id);
        galleryRepository.deleteById(id);
    }

    /**
     * 갤러리 이미지 삭제
     */
    @Transactional
    public void deleteGalleryImage(Long imageId) {
        if (!galleryImageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("갤러리 이미지를 찾을 수 없습니다: " + imageId);
        }
        galleryImageRepository.deleteById(imageId);
    }

    /**
     * 통계: 총 갤러리 개수
     */
    public long getTotalGalleryCount() {
        return galleryRepository.countByIsPublishedTrue();
    }

    /**
     * 통계: 갤러리의 이미지 개수
     */
    public long getImageCountByGallery(Long galleryId) {
        return galleryImageRepository.countByGalleryId(galleryId);
    }
}
