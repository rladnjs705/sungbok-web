package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Hymn;
import com.sungbok.church.dto.request.HymnRequest;
import com.sungbok.church.dto.response.HymnResponse;
import com.sungbok.church.service.HymnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hymn Controller
 * 찬양 관리 REST API
 */
@Tag(name = "찬송가", description = "찬송가 정보 관리 API")
@RestController
@RequestMapping("/api/hymns")
@RequiredArgsConstructor
public class HymnController {

    private final HymnService hymnService;

    /**
     * 찬양 번호로 조회
     */
    @GetMapping("/number/{hymnNumber}")
    public ResponseEntity<HymnResponse> getHymnByNumber(@PathVariable Integer hymnNumber) {
        Hymn hymn = hymnService.getHymnByNumber(hymnNumber);
        return ResponseEntity.ok(HymnResponse.from(hymn));
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search/title")
    public ResponseEntity<Page<HymnResponse>> searchByTitle(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<HymnResponse> hymns = hymnService.searchByTitle(keyword, pageable)
            .map(HymnResponse::from);
        return ResponseEntity.ok(hymns);
    }

    /**
     * 가사로 검색
     */
    @GetMapping("/search/lyrics")
    public ResponseEntity<Page<HymnResponse>> searchByLyrics(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<HymnResponse> hymns = hymnService.searchByLyrics(keyword, pageable)
            .map(HymnResponse::from);
        return ResponseEntity.ok(hymns);
    }

    /**
     * 제목 또는 가사로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HymnResponse>> searchByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<HymnResponse> hymns = hymnService.searchByKeyword(keyword, pageable)
            .map(HymnResponse::from);
        return ResponseEntity.ok(hymns);
    }

    /**
     * 많이 연주된 찬양 조회
     */
    @GetMapping("/popular")
    public ResponseEntity<List<HymnResponse>> getPopularHymns() {
        List<HymnResponse> hymns = hymnService.getPopularHymns().stream()
            .map(HymnResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(hymns);
    }

    /**
     * 최근 추가된 찬양 조회
     */
    @GetMapping("/recent")
    public ResponseEntity<List<HymnResponse>> getRecentHymns() {
        List<HymnResponse> hymns = hymnService.getRecentHymns().stream()
            .map(HymnResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(hymns);
    }

    /**
     * 찬양 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<HymnResponse> getHymnById(@PathVariable Long id) {
        Hymn hymn = hymnService.getHymnById(id);
        return ResponseEntity.ok(HymnResponse.from(hymn));
    }

    /**
     * 찬양 생성
     */
    @PostMapping
    public ResponseEntity<HymnResponse> createHymn(@Valid @RequestBody HymnRequest request) {
        Hymn hymn = toEntity(request);
        Hymn created = hymnService.createHymn(hymn);
        return ResponseEntity.status(HttpStatus.CREATED).body(HymnResponse.from(created));
    }

    /**
     * 연주 횟수 증가
     */
    @PatchMapping("/{id}/perform")
    public ResponseEntity<Void> incrementPerformanceCount(@PathVariable Long id) {
        hymnService.incrementPerformanceCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 찬양 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<HymnResponse> updateHymn(
            @PathVariable Long id,
            @Valid @RequestBody HymnRequest request) {
        Hymn hymn = toEntity(request);
        Hymn updated = hymnService.updateHymn(id, hymn);
        return ResponseEntity.ok(HymnResponse.from(updated));
    }

    /**
     * 찬양 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHymn(@PathVariable Long id) {
        hymnService.deleteHymn(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Hymn toEntity(HymnRequest request) {
        return Hymn.builder()
            .hymnNumber(request.getHymnNumber())
            .title(request.getTitle())
            .composer(request.getComposer())
            .lyricist(request.getLyricist())
            .lyrics(request.getLyrics())
            .youtubeUrl(request.getYoutubeUrl())
            .sheetMusicUrl(request.getSheetMusicUrl())
            .build();
    }
}
