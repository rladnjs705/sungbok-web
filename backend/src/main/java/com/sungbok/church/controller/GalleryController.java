package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.domain.entity.GalleryImage;
import com.sungbok.church.dto.request.GalleryImageRequest;
import com.sungbok.church.dto.request.GalleryRequest;
import com.sungbok.church.dto.response.GalleryImageResponse;
import com.sungbok.church.dto.response.GalleryResponse;
import com.sungbok.church.service.GalleryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gallery Controller
 * 갤러리 관리 REST API
 */
@Tag(name = "갤러리", description = "사진첩 관리 API")
@RestController
@RequestMapping("/api/galleries")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    /**
     * 공개된 갤러리 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<GalleryResponse>> getPublishedGalleries(
            @PageableDefault(size = 20, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GalleryResponse> galleries = galleryService.getPublishedGalleries(pageable)
            .map(GalleryResponse::from);
        return ResponseEntity.ok(galleries);
    }

    /**
     * 갤러리 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<GalleryResponse> getGalleryById(@PathVariable Long id) {
        Gallery gallery = galleryService.getGalleryById(id);
        return ResponseEntity.ok(GalleryResponse.from(gallery));
    }

    /**
     * 갤러리 이미지 목록 조회
     */
    @GetMapping("/{galleryId}/images")
    public ResponseEntity<List<GalleryImageResponse>> getGalleryImages(@PathVariable Long galleryId) {
        List<GalleryImageResponse> images = galleryService.getGalleryImages(galleryId).stream()
            .map(GalleryImageResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(images);
    }

    /**
     * 날짜 범위로 갤러리 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<GalleryResponse>> getGalleriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<GalleryResponse> galleries = galleryService.getGalleriesByDateRange(startDate, endDate, pageable)
            .map(GalleryResponse::from);
        return ResponseEntity.ok(galleries);
    }

    /**
     * 최신 갤러리 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<List<GalleryResponse>> getLatestGalleries() {
        List<GalleryResponse> galleries = galleryService.getLatestGalleries().stream()
            .map(GalleryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(galleries);
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search/title")
    public ResponseEntity<Page<GalleryResponse>> searchByTitle(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<GalleryResponse> galleries = galleryService.searchByTitle(keyword, pageable)
            .map(GalleryResponse::from);
        return ResponseEntity.ok(galleries);
    }

    /**
     * 제목 또는 설명으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<GalleryResponse>> searchByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<GalleryResponse> galleries = galleryService.searchByKeyword(keyword, pageable)
            .map(GalleryResponse::from);
        return ResponseEntity.ok(galleries);
    }

    /**
     * 갤러리 생성
     */
    @PostMapping
    public ResponseEntity<GalleryResponse> createGallery(@Valid @RequestBody GalleryRequest request) {
        Gallery gallery = toGalleryEntity(request);
        Gallery created = galleryService.createGallery(gallery);
        return ResponseEntity.status(HttpStatus.CREATED).body(GalleryResponse.from(created));
    }

    /**
     * 갤러리 이미지 추가
     */
    @PostMapping("/{galleryId}/images")
    public ResponseEntity<GalleryImageResponse> addGalleryImage(
            @PathVariable Long galleryId,
            @Valid @RequestBody GalleryImageRequest request) {
        GalleryImage image = toImageEntity(request);
        GalleryImage added = galleryService.addGalleryImage(galleryId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(GalleryImageResponse.from(added));
    }

    /**
     * 갤러리 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<GalleryResponse> updateGallery(
            @PathVariable Long id,
            @Valid @RequestBody GalleryRequest request) {
        Gallery gallery = toGalleryEntity(request);
        Gallery updated = galleryService.updateGallery(id, gallery);
        return ResponseEntity.ok(GalleryResponse.from(updated));
    }

    /**
     * 갤러리 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGallery(@PathVariable Long id) {
        galleryService.deleteGallery(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 갤러리 이미지 삭제
     */
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteGalleryImage(@PathVariable Long imageId) {
        galleryService.deleteGalleryImage(imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 총 갤러리 개수
     */
    @GetMapping("/stats/total")
    public ResponseEntity<Long> getTotalGalleryCount() {
        return ResponseEntity.ok(galleryService.getTotalGalleryCount());
    }

    /**
     * 통계: 갤러리의 이미지 개수
     */
    @GetMapping("/{galleryId}/stats/images")
    public ResponseEntity<Long> getImageCountByGallery(@PathVariable Long galleryId) {
        return ResponseEntity.ok(galleryService.getImageCountByGallery(galleryId));
    }

    /**
     * Request DTO -> Gallery Entity 변환
     */
    private Gallery toGalleryEntity(GalleryRequest request) {
        return Gallery.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .eventDate(request.getEventDate())
            .coverImageUrl(request.getCoverImageUrl())
            .isPublished(request.getIsPublished())
            .build();
    }

    /**
     * Request DTO -> GalleryImage Entity 변환
     */
    private GalleryImage toImageEntity(GalleryImageRequest request) {
        return GalleryImage.builder()
            .imageUrl(request.getImageUrl())
            .thumbnailUrl(request.getThumbnailUrl())
            .caption(request.getCaption())
            .fileSize(request.getFileSize())
            .width(request.getWidth())
            .height(request.getHeight())
            .displayOrder(request.getDisplayOrder())
            .build();
    }
}
