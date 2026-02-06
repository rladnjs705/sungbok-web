package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Bulletin;
import com.sungbok.church.dto.request.BulletinRequest;
import com.sungbok.church.dto.response.BulletinResponse;
import com.sungbok.church.service.BulletinService;
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
 * Bulletin Controller
 * 주보 관리 REST API
 */
@Tag(name = "주보", description = "주보 관리 API")
@RestController
@RequestMapping("/api/bulletins")
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinService bulletinService;

    /**
     * 공개된 주보 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<BulletinResponse>> getPublishedBulletins(
            @PageableDefault(size = 20, sort = "bulletinDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BulletinResponse> bulletins = bulletinService.getPublishedBulletins(pageable)
            .map(BulletinResponse::from);
        return ResponseEntity.ok(bulletins);
    }

    /**
     * 주보 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<BulletinResponse> getBulletinById(@PathVariable Long id) {
        Bulletin bulletin = bulletinService.getBulletinById(id);
        return ResponseEntity.ok(BulletinResponse.from(bulletin));
    }

    /**
     * 날짜로 주보 조회
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<BulletinResponse> getBulletinByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Bulletin bulletin = bulletinService.getBulletinByDate(date);
        return ResponseEntity.ok(BulletinResponse.from(bulletin));
    }

    /**
     * 날짜 범위로 주보 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<BulletinResponse>> getBulletinsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BulletinResponse> bulletins = bulletinService.getBulletinsByDateRange(startDate, endDate, pageable)
            .map(BulletinResponse::from);
        return ResponseEntity.ok(bulletins);
    }

    /**
     * 최신 주보 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<List<BulletinResponse>> getLatestBulletins() {
        List<BulletinResponse> bulletins = bulletinService.getLatestBulletins().stream()
            .map(BulletinResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(bulletins);
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BulletinResponse>> searchByTitle(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BulletinResponse> bulletins = bulletinService.searchByTitle(keyword, pageable)
            .map(BulletinResponse::from);
        return ResponseEntity.ok(bulletins);
    }

    /**
     * 주보 다운로드
     */
    @PostMapping("/{id}/download")
    public ResponseEntity<BulletinResponse> downloadBulletin(@PathVariable Long id) {
        Bulletin bulletin = bulletinService.downloadBulletin(id);
        return ResponseEntity.ok(BulletinResponse.from(bulletin));
    }

    /**
     * 주보 생성
     */
    @PostMapping
    public ResponseEntity<BulletinResponse> createBulletin(@Valid @RequestBody BulletinRequest request) {
        Bulletin bulletin = toEntity(request);
        Bulletin created = bulletinService.createBulletin(bulletin);
        return ResponseEntity.status(HttpStatus.CREATED).body(BulletinResponse.from(created));
    }

    /**
     * 주보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<BulletinResponse> updateBulletin(
            @PathVariable Long id,
            @Valid @RequestBody BulletinRequest request) {
        Bulletin bulletin = toEntity(request);
        Bulletin updated = bulletinService.updateBulletin(id, bulletin);
        return ResponseEntity.ok(BulletinResponse.from(updated));
    }

    /**
     * 주보 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBulletin(@PathVariable Long id) {
        bulletinService.deleteBulletin(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 총 주보 개수
     */
    @GetMapping("/stats/total")
    public ResponseEntity<Long> getTotalBulletinCount() {
        return ResponseEntity.ok(bulletinService.getTotalBulletinCount());
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Bulletin toEntity(BulletinRequest request) {
        return Bulletin.builder()
            .bulletinDate(request.getBulletinDate())
            .title(request.getTitle())
            .pdfUrl(request.getPdfUrl())
            .fileSize(request.getFileSize())
            .thumbnailUrl(request.getThumbnailUrl())
            .isPublished(request.getIsPublished())
            .build();
    }
}
