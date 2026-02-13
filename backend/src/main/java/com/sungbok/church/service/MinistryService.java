package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Ministry;
import com.sungbok.church.domain.entity.Mission;
import com.sungbok.church.domain.enums.MinistryCategory;
import com.sungbok.church.domain.enums.MissionType;
import com.sungbok.church.domain.repository.MinistryRepository;
import com.sungbok.church.domain.repository.MissionRepository;
import com.sungbok.church.dto.projection.MissionProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Ministry Service
 * 교육/양육 부서 및 선교 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MinistryService {

    private final MinistryRepository ministryRepository;
    private final MissionRepository missionRepository;

    // ========== Ministry (교육/양육 부서) ==========

    /**
     * 활성화된 부서 목록 조회
     */
    public List<Ministry> getActiveMinistries() {
        return ministryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * 카테고리별 부서 조회
     */
    public List<Ministry> getMinistriesByCategory(MinistryCategory category) {
        return ministryRepository.findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(category);
    }

    /**
     * 대상 연령별 부서 조회
     */
    public List<Ministry> getMinistriesByTargetAge(String targetAge) {
        return ministryRepository.findByTargetAgeAndIsActiveTrue(targetAge);
    }

    /**
     * 부서 이름으로 검색
     */
    public List<Ministry> searchMinistriesByName(String keyword) {
        return ministryRepository.findByNameContainingAndIsActiveTrue(keyword);
    }

    /**
     * 부서 생성
     */
    @Transactional
    public Ministry createMinistry(Ministry ministry) {
        return ministryRepository.save(ministry);
    }

    /**
     * 부서 수정
     */
    @Transactional
    public Ministry updateMinistry(Long id, Ministry updatedMinistry) {
        Ministry ministry = ministryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다: " + id));

        ministry.setName(updatedMinistry.getName());
        ministry.setCategory(updatedMinistry.getCategory());
        ministry.setDescription(updatedMinistry.getDescription());
        ministry.setTargetAge(updatedMinistry.getTargetAge());
        ministry.setSchedule(updatedMinistry.getSchedule());
        ministry.setLocation(updatedMinistry.getLocation());
        ministry.setLeader(updatedMinistry.getLeader());
        ministry.setContact(updatedMinistry.getContact());
        ministry.setPhotoUrl(updatedMinistry.getPhotoUrl());
        ministry.setIsActive(updatedMinistry.getIsActive());
        ministry.setDisplayOrder(updatedMinistry.getDisplayOrder());

        return ministryRepository.save(ministry);
    }

    /**
     * 부서 삭제
     */
    @Transactional
    public void deleteMinistry(Long id) {
        if (!ministryRepository.existsById(id)) {
            throw new IllegalArgumentException("부서를 찾을 수 없습니다: " + id);
        }
        ministryRepository.deleteById(id);
    }

    // ========== Mission (선교) ==========

    /**
     * 활성화된 선교 목록 조회
     */
    public List<Mission> getActiveMissions() {
        return missionRepository.findByIsActiveTrueOrderByStartDateDesc();
    }

    /**
     * 선교 유형별 조회
     */
    public List<Mission> getMissionsByType(MissionType type) {
        return missionRepository.findByTypeAndIsActiveTrueOrderByStartDateDesc(type);
    }

    /**
     * 국가별 선교 조회
     */
    public List<Mission> getMissionsByCountry(String country) {
        return missionRepository.findByCountryAndIsActiveTrue(country);
    }

    /**
     * 진행 중인 선교 조회
     */
    public Page<MissionProjectionDto> getOngoingMissions(Pageable pageable) {
        return missionRepository.findOngoingMissions(LocalDate.now(), pageable);
    }

    /**
     * 종료된 선교 조회
     */
    public Page<MissionProjectionDto> getCompletedMissions(Pageable pageable) {
        return missionRepository.findCompletedMissions(LocalDate.now(), pageable);
    }

    /**
     * 선교사 이름으로 검색
     */
    public List<Mission> searchMissionsByMissionary(String keyword) {
        return missionRepository.findByMissionaryNameContainingAndIsActiveTrue(keyword);
    }

    /**
     * 선교 생성
     */
    @Transactional
    public Mission createMission(Mission mission) {
        return missionRepository.save(mission);
    }

    /**
     * 선교 수정
     */
    @Transactional
    public Mission updateMission(Long id, Mission updatedMission) {
        Mission mission = missionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("선교를 찾을 수 없습니다: " + id));

        mission.setTitle(updatedMission.getTitle());
        mission.setType(updatedMission.getType());
        mission.setCountry(updatedMission.getCountry());
        mission.setRegion(updatedMission.getRegion());
        mission.setDescription(updatedMission.getDescription());
        mission.setMissionaryName(updatedMission.getMissionaryName());
        mission.setStartDate(updatedMission.getStartDate());
        mission.setEndDate(updatedMission.getEndDate());
        mission.setSupportAmount(updatedMission.getSupportAmount());
        mission.setPhotoUrl(updatedMission.getPhotoUrl());
        mission.setIsActive(updatedMission.getIsActive());

        return missionRepository.save(mission);
    }

    /**
     * 선교 삭제
     */
    @Transactional
    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new IllegalArgumentException("선교를 찾을 수 없습니다: " + id);
        }
        missionRepository.deleteById(id);
    }

    // ========== 통계 ==========

    /**
     * 카테고리별 부서 개수
     */
    public long getMinistryCountByCategory(MinistryCategory category) {
        return ministryRepository.countByCategoryAndIsActive(category, true);
    }

    /**
     * 선교 유형별 개수
     */
    public long getMissionCountByType(MissionType type) {
        return missionRepository.countByTypeAndIsActive(type, true);
    }

    /**
     * 총 후원금액 집계
     */
    public Long getTotalSupportAmount() {
        return missionRepository.calculateTotalSupportAmount();
    }
}
