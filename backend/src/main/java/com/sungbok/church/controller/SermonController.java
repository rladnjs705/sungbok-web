package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.repository.WorshipRepository;
import com.sungbok.church.dto.request.SermonRequest;
import com.sungbok.church.dto.response.SermonResponse;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.service.SermonService;
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
 * Sermon Controller
 * 설교 관리 REST API
 */
@Tag(name = "설교", description = "설교 관리 API")
@RestController
@RequestMapping("/api/sermons")
@RequiredArgsConstructor
public class SermonController {

    private final SermonService sermonService;
    private final WorshipRepository worshipRepository;

    /**
     * 공개된 설교 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<SermonResponse>> getPublishedSermons(
            @PageableDefault(size = 20, sort = "sermonDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SermonResponse> sermons = sermonService.getPublishedSermons(pageable)
            .map(SermonResponse::from);
        return ResponseEntity.ok(sermons);
    }

    /**
     * 설교 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<SermonResponse> getSermonById(@PathVariable Long id) {
        Sermon sermon = sermonService.getSermonById(id);
        return ResponseEntity.ok(SermonResponse.from(sermon));
    }

    /**
     * 설교자별 설교 조회
     */
    @GetMapping("/preacher/{preacher}")
    public ResponseEntity<Page<SermonResponse>> getSermonsByPreacher(
            @PathVariable String preacher,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SermonResponse> sermons = sermonService.getSermonsByPreacher(preacher, pageable)
            .map(SermonResponse::from);
        return ResponseEntity.ok(sermons);
    }

    /**
     * 날짜 범위로 설교 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<SermonResponse>> getSermonsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SermonResponse> sermons = sermonService.getSermonsByDateRange(startDate, endDate, pageable)
            .map(SermonResponse::from);
        return ResponseEntity.ok(sermons);
    }

    /**
     * 추천 설교 목록
     */
    @GetMapping("/featured")
    public ResponseEntity<List<SermonResponse>> getFeaturedSermons() {
        List<SermonResponse> sermons = sermonService.getFeaturedSermons().stream()
            .map(SermonResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(sermons);
    }

    /**
     * 최신 설교 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<List<SermonResponse>> getLatestSermons() {
        List<SermonResponse> sermons = sermonService.getLatestSermons().stream()
            .map(SermonResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(sermons);
    }

    /**
     * 본문으로 검색
     */
    @GetMapping("/search/verse")
    public ResponseEntity<Page<SermonResponse>> searchByBibleVerse(
            @RequestParam String verse,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SermonResponse> sermons = sermonService.searchByBibleVerse(verse, pageable)
            .map(SermonResponse::from);
        return ResponseEntity.ok(sermons);
    }

    /**
     * 태그로 검색
     */
    @GetMapping("/search/tag")
    public ResponseEntity<Page<SermonResponse>> searchByTag(
            @RequestParam String tag,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SermonResponse> sermons = sermonService.searchByTag(tag, pageable)
            .map(SermonResponse::from);
        return ResponseEntity.ok(sermons);
    }

    /**
     * 설교 생성
     */
    @PostMapping
    public ResponseEntity<SermonResponse> createSermon(@Valid @RequestBody SermonRequest request) {
        Sermon sermon = toEntity(request);
        Sermon created = sermonService.createSermon(sermon);
        return ResponseEntity.status(HttpStatus.CREATED).body(SermonResponse.from(created));
    }

    /**
     * 설교 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<SermonResponse> updateSermon(
            @PathVariable Long id,
            @Valid @RequestBody SermonRequest request) {
        Sermon sermon = toEntity(request);
        Sermon updated = sermonService.updateSermon(id, sermon);
        return ResponseEntity.ok(SermonResponse.from(updated));
    }

    /**
     * 설교 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSermon(@PathVariable Long id) {
        sermonService.deleteSermon(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 총 설교 개수
     */
    @GetMapping("/stats/total")
    public ResponseEntity<Long> getTotalSermonCount() {
        return ResponseEntity.ok(sermonService.getTotalSermonCount());
    }

    /**
     * 통계: 설교자별 설교 개수
     */
    @GetMapping("/stats/preacher/{preacher}")
    public ResponseEntity<Long> getSermonCountByPreacher(@PathVariable String preacher) {
        return ResponseEntity.ok(sermonService.getSermonCountByPreacher(preacher));
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Sermon toEntity(SermonRequest request) {
        Sermon.SermonBuilder builder = Sermon.builder()
            .title(request.getTitle())
            .preacher(request.getPreacher())
            .sermonDate(request.getSermonDate())
            .bibleVerse(request.getBibleVerse())
            .summary(request.getSummary())
            .youtubeVideoId(request.getYoutubeVideoId())
            .thumbnailUrl(request.getThumbnailUrl())
            .duration(request.getDuration())
            .tags(request.getTags())
            .isFeatured(request.getIsFeatured())
            .isPublished(request.getIsPublished());

        // Worship 관계 설정
        if (request.getWorshipId() != null) {
            Worship worship = worshipRepository.findById(request.getWorshipId())
                .orElseThrow(() -> new ResourceNotFoundException("예배를 찾을 수 없습니다: " + request.getWorshipId()));
            builder.worship(worship);
        }

        return builder.build();
    }
}
