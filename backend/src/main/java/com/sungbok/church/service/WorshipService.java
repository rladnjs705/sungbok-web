package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.repository.WorshipRepository;
import com.sungbok.church.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Worship Service
 * 예배 정보 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorshipService {

    private final WorshipRepository worshipRepository;

    /**
     * 활성화된 예배 목록 조회
     */
    public List<Worship> getActiveWorships() {
        return worshipRepository.findByIsActiveTrueOrderByDayOfWeekAsc();
    }

    /**
     * 예배 유형별 조회
     */
    public List<Worship> getWorshipsByType(WorshipType type) {
        return worshipRepository.findByTypeAndIsActiveTrue(type);
    }

    /**
     * 요일별 예배 조회
     */
    public List<Worship> getWorshipsByDayOfWeek(DayOfWeek dayOfWeek) {
        return worshipRepository.findByDayOfWeekAndIsActiveTrue(dayOfWeek);
    }

    /**
     * 현재 라이브 중인 예배 조회
     */
    public Optional<Worship> getLiveWorship() {
        return worshipRepository.findByIsLiveNowTrue();
    }

    /**
     * 라이브 스트리밍 예배 목록
     */
    public List<Worship> getLiveStreamWorships() {
        return worshipRepository.findByLiveStreamUrlIsNotNullAndIsActiveTrue();
    }

    /**
     * 예배 생성
     */
    @Transactional
    public Worship createWorship(Worship worship) {
        validateWorshipUniqueness(worship.getType(), worship.getDayOfWeek());
        return worshipRepository.save(worship);
    }

    /**
     * 예배 수정
     */
    @Transactional
    public Worship updateWorship(Long id, Worship updatedWorship) {
        Worship worship = worshipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("예배를 찾을 수 없습니다: " + id));

        worship.setTitle(updatedWorship.getTitle());
        worship.setType(updatedWorship.getType());
        worship.setDayOfWeek(updatedWorship.getDayOfWeek());
        worship.setStartTime(updatedWorship.getStartTime());
        worship.setLocation(updatedWorship.getLocation());
        worship.setDescription(updatedWorship.getDescription());
        worship.setLiveStreamUrl(updatedWorship.getLiveStreamUrl());
        worship.setIsActive(updatedWorship.getIsActive());

        return worshipRepository.save(worship);
    }

    /**
     * 라이브 상태 토글
     */
    @Transactional
    public void toggleLiveStatus(Long id, boolean isLive) {
        Worship worship = worshipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("예배를 찾을 수 없습니다: " + id));

        // 다른 예배의 라이브 상태 해제
        if (isLive) {
            worshipRepository.findByIsLiveNowTrue()
                .ifPresent(liveWorship -> {
                    liveWorship.setIsLiveNow(false);
                    worshipRepository.save(liveWorship);
                });
        }

        worship.setIsLiveNow(isLive);
        worshipRepository.save(worship);
    }

    /**
     * 예배 삭제
     */
    @Transactional
    public void deleteWorship(Long id) {
        if (!worshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("예배를 찾을 수 없습니다: " + id);
        }
        worshipRepository.deleteById(id);
    }

    /**
     * 예배 중복 체크
     */
    private void validateWorshipUniqueness(WorshipType type, DayOfWeek dayOfWeek) {
        if (worshipRepository.existsByTypeAndDayOfWeek(type, dayOfWeek)) {
            throw new ResourceNotFoundException(
                String.format("이미 존재하는 예배입니다: %s %s", dayOfWeek, type)
            );
        }
    }
}
