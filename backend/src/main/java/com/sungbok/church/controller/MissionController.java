package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Mission;
import com.sungbok.church.domain.enums.MissionType;
import com.sungbok.church.dto.request.MissionRequest;
import com.sungbok.church.dto.response.MissionResponse;
import com.sungbok.church.service.MinistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mission Controller
 * 선교 관리 REST API
 */
@Tag(name = "선교", description = "선교 활동 관리 API")
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MinistryService ministryService;

    /**
     * 활성화된 선교 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<MissionResponse>> getActiveMissions() {
        List<MissionResponse> missions = ministryService.getActiveMissions().stream()
            .map(MissionResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(missions);
    }

    /**
     * 선교 유형별 조회
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MissionResponse>> getMissionsByType(@PathVariable MissionType type) {
        List<MissionResponse> missions = ministryService.getMissionsByType(type).stream()
            .map(MissionResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(missions);
    }

    /**
     * 국가별 선교 조회
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<MissionResponse>> getMissionsByCountry(@PathVariable String country) {
        List<MissionResponse> missions = ministryService.getMissionsByCountry(country).stream()
            .map(MissionResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(missions);
    }

    /**
     * 진행 중인 선교 조회
     */
    @GetMapping("/ongoing")
    public ResponseEntity<Page<MissionResponse>> getOngoingMissions(Pageable pageable) {
        Page<MissionResponse> missions = ministryService.getOngoingMissions(pageable)
            .map(MissionResponse::from);
        return ResponseEntity.ok(missions);
    }

    /**
     * 종료된 선교 조회
     */
    @GetMapping("/completed")
    public ResponseEntity<Page<MissionResponse>> getCompletedMissions(Pageable pageable) {
        Page<MissionResponse> missions = ministryService.getCompletedMissions(pageable)
            .map(MissionResponse::from);
        return ResponseEntity.ok(missions);
    }

    /**
     * 선교사 이름으로 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<MissionResponse>> searchMissionsByMissionary(@RequestParam String keyword) {
        List<MissionResponse> missions = ministryService.searchMissionsByMissionary(keyword).stream()
            .map(MissionResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(missions);
    }

    /**
     * 선교 생성
     */
    @PostMapping
    public ResponseEntity<MissionResponse> createMission(@Valid @RequestBody MissionRequest request) {
        Mission mission = toEntity(request);
        Mission created = ministryService.createMission(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(MissionResponse.from(created));
    }

    /**
     * 선교 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<MissionResponse> updateMission(
            @PathVariable Long id,
            @Valid @RequestBody MissionRequest request) {
        Mission mission = toEntity(request);
        Mission updated = ministryService.updateMission(id, mission);
        return ResponseEntity.ok(MissionResponse.from(updated));
    }

    /**
     * 선교 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        ministryService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 선교 유형별 개수
     */
    @GetMapping("/stats/type/{type}")
    public ResponseEntity<Long> getMissionCountByType(@PathVariable MissionType type) {
        return ResponseEntity.ok(ministryService.getMissionCountByType(type));
    }

    /**
     * 통계: 총 후원금액
     */
    @GetMapping("/stats/support-amount")
    public ResponseEntity<Long> getTotalSupportAmount() {
        Long total = ministryService.getTotalSupportAmount();
        return ResponseEntity.ok(total != null ? total : 0L);
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Mission toEntity(MissionRequest request) {
        return Mission.builder()
            .title(request.getTitle())
            .type(request.getType())
            .country(request.getCountry())
            .region(request.getRegion())
            .description(request.getDescription())
            .missionaryName(request.getMissionaryName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .supportAmount(request.getSupportAmount())
            .photoUrl(request.getPhotoUrl())
            .isActive(request.getIsActive())
            .build();
    }
}
