package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Ministry;
import com.sungbok.church.domain.enums.MinistryCategory;
import com.sungbok.church.dto.request.MinistryRequest;
import com.sungbok.church.dto.response.MinistryResponse;
import com.sungbok.church.service.MinistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ministry Controller
 * 교육/양육 부서 관리 REST API
 */
@Tag(name = "사역", description = "사역 관리 API")
@RestController
@RequestMapping("/api/ministries")
@RequiredArgsConstructor
public class MinistryController {

    private final MinistryService ministryService;

    /**
     * 활성화된 부서 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<MinistryResponse>> getActiveMinistries() {
        List<MinistryResponse> ministries = ministryService.getActiveMinistries().stream()
            .map(MinistryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ministries);
    }

    /**
     * 카테고리별 부서 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MinistryResponse>> getMinistriesByCategory(
            @PathVariable MinistryCategory category) {
        List<MinistryResponse> ministries = ministryService.getMinistriesByCategory(category).stream()
            .map(MinistryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ministries);
    }

    /**
     * 대상 연령별 부서 조회
     */
    @GetMapping("/target-age/{targetAge}")
    public ResponseEntity<List<MinistryResponse>> getMinistriesByTargetAge(
            @PathVariable String targetAge) {
        List<MinistryResponse> ministries = ministryService.getMinistriesByTargetAge(targetAge).stream()
            .map(MinistryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ministries);
    }

    /**
     * 부서 이름으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<MinistryResponse>> searchMinistriesByName(@RequestParam String keyword) {
        List<MinistryResponse> ministries = ministryService.searchMinistriesByName(keyword).stream()
            .map(MinistryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ministries);
    }

    /**
     * 부서 생성
     */
    @PostMapping
    public ResponseEntity<MinistryResponse> createMinistry(@Valid @RequestBody MinistryRequest request) {
        Ministry ministry = toEntity(request);
        Ministry created = ministryService.createMinistry(ministry);
        return ResponseEntity.status(HttpStatus.CREATED).body(MinistryResponse.from(created));
    }

    /**
     * 부서 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<MinistryResponse> updateMinistry(
            @PathVariable Long id,
            @Valid @RequestBody MinistryRequest request) {
        Ministry ministry = toEntity(request);
        Ministry updated = ministryService.updateMinistry(id, ministry);
        return ResponseEntity.ok(MinistryResponse.from(updated));
    }

    /**
     * 부서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMinistry(@PathVariable Long id) {
        ministryService.deleteMinistry(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 카테고리별 부서 개수
     */
    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Long> getMinistryCountByCategory(@PathVariable MinistryCategory category) {
        return ResponseEntity.ok(ministryService.getMinistryCountByCategory(category));
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Ministry toEntity(MinistryRequest request) {
        return Ministry.builder()
            .name(request.getName())
            .category(request.getCategory())
            .description(request.getDescription())
            .targetAge(request.getTargetAge())
            .schedule(request.getSchedule())
            .location(request.getLocation())
            .leader(request.getLeader())
            .contact(request.getContact())
            .photoUrl(request.getPhotoUrl())
            .isActive(request.getIsActive())
            .displayOrder(request.getDisplayOrder())
            .build();
    }
}
