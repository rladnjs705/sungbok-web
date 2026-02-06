package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Staff;
import com.sungbok.church.domain.enums.StaffRole;
import com.sungbok.church.domain.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Staff Service
 * 교회 직원 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffService {

    private final StaffRepository staffRepository;

    /**
     * 활성화된 직원 목록 조회
     */
    public List<Staff> getActiveStaff() {
        return staffRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * 역할별 직원 조회
     */
    public List<Staff> getStaffByRole(StaffRole role) {
        return staffRepository.findByRoleAndIsActiveTrueOrderByDisplayOrderAsc(role);
    }

    /**
     * 부서별 직원 조회
     */
    public List<Staff> getStaffByDepartment(String department) {
        return staffRepository.findByDepartmentAndIsActiveTrue(department);
    }

    /**
     * 직원 ID로 조회
     */
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다: " + id));
    }

    /**
     * 직원 생성
     */
    @Transactional
    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    /**
     * 직원 수정
     */
    @Transactional
    public Staff updateStaff(Long id, Staff updatedStaff) {
        Staff staff = getStaffById(id);

        staff.setName(updatedStaff.getName());
        staff.setRole(updatedStaff.getRole());
        staff.setDepartment(updatedStaff.getDepartment());
        staff.setPosition(updatedStaff.getPosition());
        staff.setEmail(updatedStaff.getEmail());
        staff.setPhone(updatedStaff.getPhone());
        staff.setPhotoUrl(updatedStaff.getPhotoUrl());
        staff.setBio(updatedStaff.getBio());
        staff.setIsActive(updatedStaff.getIsActive());
        staff.setDisplayOrder(updatedStaff.getDisplayOrder());

        return staffRepository.save(staff);
    }

    /**
     * 직원 삭제
     */
    @Transactional
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new IllegalArgumentException("직원을 찾을 수 없습니다: " + id);
        }
        staffRepository.deleteById(id);
    }
}
