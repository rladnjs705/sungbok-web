package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.VideoGallery;
import com.sungbok.church.dto.request.VideoGalleryRequest;
import com.sungbok.church.dto.response.VideoGalleryResponse;
import com.sungbok.church.service.VideoGalleryService;
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
 * VideoGallery Controller
 * 동영상 갤러리 관리 REST API
 */
@Tag(name = "영상 갤러리", description = "영상 갤러리 관리 API")
@RestController
@RequestMapping("/api/video-galleries")
@RequiredArgsConstructor
public class VideoGalleryController {

    private final VideoGalleryService videoGalleryService;

    /**
     * 공개된 동영상 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<VideoGalleryResponse>> getPublishedVideos(
            @PageableDefault(size = 20, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<VideoGalleryResponse> videos = videoGalleryService.getPublishedVideos(pageable)
            .map(VideoGalleryResponse::from);
        return ResponseEntity.ok(videos);
    }

    /**
     * 카테고리별 동영상 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<VideoGalleryResponse>> getVideosByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VideoGalleryResponse> videos = videoGalleryService.getVideosByCategory(category, pageable)
            .map(VideoGalleryResponse::from);
        return ResponseEntity.ok(videos);
    }

    /**
     * 날짜 범위로 동영상 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<VideoGalleryResponse>> getVideosByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VideoGalleryResponse> videos = videoGalleryService.getVideosByDateRange(startDate, endDate, pageable)
            .map(VideoGalleryResponse::from);
        return ResponseEntity.ok(videos);
    }

    /**
     * 동영상 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<VideoGalleryResponse> getVideoById(@PathVariable Long id) {
        VideoGallery video = videoGalleryService.getVideoById(id);
        return ResponseEntity.ok(VideoGalleryResponse.from(video));
    }

    /**
     * 최신 동영상 조회
     */
    @GetMapping("/latest")
    public ResponseEntity<List<VideoGalleryResponse>> getLatestVideos() {
        List<VideoGalleryResponse> videos = videoGalleryService.getLatestVideos().stream()
            .map(VideoGalleryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(videos);
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search/title")
    public ResponseEntity<Page<VideoGalleryResponse>> searchByTitle(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VideoGalleryResponse> videos = videoGalleryService.searchByTitle(keyword, pageable)
            .map(VideoGalleryResponse::from);
        return ResponseEntity.ok(videos);
    }

    /**
     * 제목 또는 설명으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<VideoGalleryResponse>> searchByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VideoGalleryResponse> videos = videoGalleryService.searchByKeyword(keyword, pageable)
            .map(VideoGalleryResponse::from);
        return ResponseEntity.ok(videos);
    }

    /**
     * 동영상 생성
     */
    @PostMapping
    public ResponseEntity<VideoGalleryResponse> createVideo(@Valid @RequestBody VideoGalleryRequest request) {
        VideoGallery video = toEntity(request);
        VideoGallery created = videoGalleryService.createVideo(video);
        return ResponseEntity.status(HttpStatus.CREATED).body(VideoGalleryResponse.from(created));
    }

    /**
     * 동영상 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<VideoGalleryResponse> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody VideoGalleryRequest request) {
        VideoGallery video = toEntity(request);
        VideoGallery updated = videoGalleryService.updateVideo(id, video);
        return ResponseEntity.ok(VideoGalleryResponse.from(updated));
    }

    /**
     * 동영상 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoGalleryService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 카테고리별 동영상 개수
     */
    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Long> getVideoCountByCategory(@PathVariable String category) {
        return ResponseEntity.ok(videoGalleryService.getVideoCountByCategory(category));
    }

    /**
     * Request DTO -> Entity 변환
     */
    private VideoGallery toEntity(VideoGalleryRequest request) {
        return VideoGallery.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .youtubeVideoId(request.getYoutubeVideoId())
            .videoUrl(request.getVideoUrl())
            .thumbnailUrl(request.getThumbnailUrl())
            .eventDate(request.getEventDate())
            .category(request.getCategory())
            .isPublished(request.getIsPublished())
            .build();
    }
}
