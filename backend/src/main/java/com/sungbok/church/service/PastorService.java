package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Pastor;
import com.sungbok.church.domain.repository.PastorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Pastor Service
 * 목회자 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PastorService {

    private final PastorRepository pastorRepository;

    /**
     * 활성화된 목회자 목록 조회
     */
    public List<Pastor> getActivePastors() {
        return pastorRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    /**
     * 직책별 목회자 조회
     */
    public List<Pastor> getPastorsByPosition(String position) {
        return pastorRepository.findByPositionAndIsActiveTrue(position);
    }

    /**
     * 목회자 ID로 조회
     */
    public Pastor getPastorById(Long id) {
        return pastorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("목회자를 찾을 수 없습니다: " + id));
    }

    /**
     * 목회자 생성
     */
    @Transactional
    public Pastor createPastor(Pastor pastor) {
        validateEmailUniqueness(pastor.getEmail());
        return pastorRepository.save(pastor);
    }

    /**
     * 목회자 수정
     */
    @Transactional
    public Pastor updatePastor(Long id, Pastor updatedPastor) {
        Pastor pastor = getPastorById(id);

        if (!pastor.getEmail().equals(updatedPastor.getEmail())) {
            validateEmailUniqueness(updatedPastor.getEmail());
            pastor.setEmail(updatedPastor.getEmail());
        }

        pastor.setName(updatedPastor.getName());
        pastor.setPosition(updatedPastor.getPosition());
        pastor.setPhone(updatedPastor.getPhone());
        pastor.setBio(updatedPastor.getBio());
        pastor.setPhotoUrl(updatedPastor.getPhotoUrl());
        pastor.setEducation(updatedPastor.getEducation());
        pastor.setMinistryArea(updatedPastor.getMinistryArea());
        pastor.setIsActive(updatedPastor.getIsActive());
        pastor.setDisplayOrder(updatedPastor.getDisplayOrder());

        return pastorRepository.save(pastor);
    }

    /**
     * 목회자 삭제
     */
    @Transactional
    public void deletePastor(Long id) {
        if (!pastorRepository.existsById(id)) {
            throw new IllegalArgumentException("목회자를 찾을 수 없습니다: " + id);
        }
        pastorRepository.deleteById(id);
    }

    /**
     * 이메일 중복 체크
     */
    private void validateEmailUniqueness(String email) {
        if (pastorRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + email);
        }
    }
}
