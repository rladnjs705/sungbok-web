package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Pastor;
import com.sungbok.church.dto.request.PastorRequest;
import com.sungbok.church.dto.response.PastorResponse;
import com.sungbok.church.service.PastorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pastor Controller
 * 목회자 관리 REST API
 */
@Tag(name = "목회자", description = "목회자 정보 관리 API")
@RestController
@RequestMapping("/api/pastors")
@RequiredArgsConstructor
public class PastorController {

    private final PastorService pastorService;

    /**
     * 활성화된 목회자 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<PastorResponse>> getActivePastors() {
        List<PastorResponse> pastors = pastorService.getActivePastors().stream()
            .map(PastorResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(pastors);
    }

    /**
     * 직책별 목회자 조회
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<List<PastorResponse>> getPastorsByPosition(@PathVariable String position) {
        List<PastorResponse> pastors = pastorService.getPastorsByPosition(position).stream()
            .map(PastorResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(pastors);
    }

    /**
     * 목회자 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PastorResponse> getPastorById(@PathVariable Long id) {
        Pastor pastor = pastorService.getPastorById(id);
        return ResponseEntity.ok(PastorResponse.from(pastor));
    }

    /**
     * 목회자 생성
     */
    @PostMapping
    public ResponseEntity<PastorResponse> createPastor(@Valid @RequestBody PastorRequest request) {
        Pastor pastor = toEntity(request);
        Pastor created = pastorService.createPastor(pastor);
        return ResponseEntity.status(HttpStatus.CREATED).body(PastorResponse.from(created));
    }

    /**
     * 목회자 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<PastorResponse> updatePastor(
            @PathVariable Long id,
            @Valid @RequestBody PastorRequest request) {
        Pastor pastor = toEntity(request);
        Pastor updated = pastorService.updatePastor(id, pastor);
        return ResponseEntity.ok(PastorResponse.from(updated));
    }

    /**
     * 목회자 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePastor(@PathVariable Long id) {
        pastorService.deletePastor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Pastor toEntity(PastorRequest request) {
        return Pastor.builder()
            .name(request.getName())
            .position(request.getPosition())
            .email(request.getEmail())
            .phone(request.getPhone())
            .bio(request.getBio())
            .photoUrl(request.getPhotoUrl())
            .education(request.getEducation())
            .ministryArea(request.getMinistryArea())
            .isActive(request.getIsActive())
            .displayOrder(request.getDisplayOrder())
            .build();
    }
}
