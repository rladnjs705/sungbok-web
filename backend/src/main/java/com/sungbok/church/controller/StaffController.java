package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Staff;
import com.sungbok.church.domain.enums.StaffRole;
import com.sungbok.church.dto.request.StaffRequest;
import com.sungbok.church.dto.response.StaffResponse;
import com.sungbok.church.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Staff Controller
 * 교회 직원 관리 REST API
 */
@Tag(name = "교직원", description = "교직원 관리 API")
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    /**
     * 활성화된 직원 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<StaffResponse>> getActiveStaff() {
        List<StaffResponse> staff = staffService.getActiveStaff().stream()
            .map(StaffResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }

    /**
     * 역할별 직원 조회
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<StaffResponse>> getStaffByRole(@PathVariable StaffRole role) {
        List<StaffResponse> staff = staffService.getStaffByRole(role).stream()
            .map(StaffResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }

    /**
     * 부서별 직원 조회
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<StaffResponse>> getStaffByDepartment(@PathVariable String department) {
        List<StaffResponse> staff = staffService.getStaffByDepartment(department).stream()
            .map(StaffResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }

    /**
     * 직원 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {
        Staff staff = staffService.getStaffById(id);
        return ResponseEntity.ok(StaffResponse.from(staff));
    }

    /**
     * 직원 생성
     */
    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody StaffRequest request) {
        Staff staff = toEntity(request);
        Staff created = staffService.createStaff(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(StaffResponse.from(created));
    }

    /**
     * 직원 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody StaffRequest request) {
        Staff staff = toEntity(request);
        Staff updated = staffService.updateStaff(id, staff);
        return ResponseEntity.ok(StaffResponse.from(updated));
    }

    /**
     * 직원 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Staff toEntity(StaffRequest request) {
        return Staff.builder()
            .name(request.getName())
            .role(request.getRole())
            .department(request.getDepartment())
            .position(request.getPosition())
            .email(request.getEmail())
            .phone(request.getPhone())
            .photoUrl(request.getPhotoUrl())
            .bio(request.getBio())
            .isActive(request.getIsActive())
            .displayOrder(request.getDisplayOrder())
            .build();
    }
}
