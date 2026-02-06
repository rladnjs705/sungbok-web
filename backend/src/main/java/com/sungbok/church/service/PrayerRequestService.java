package com.sungbok.church.service;

import com.sungbok.church.domain.entity.PrayerRequest;
import com.sungbok.church.domain.enums.PrayerStatus;
import com.sungbok.church.domain.repository.PrayerRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PrayerRequest Service
 * 기도요청 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrayerRequestService {

    private final PrayerRequestRepository prayerRequestRepository;

    /**
     * 승인된 기도요청 목록 조회 (페이징)
     */
    public Page<PrayerRequest> getApprovedPrayerRequests(Pageable pageable) {
        return prayerRequestRepository.findByIsApprovedTrueOrderByCreatedAtDesc(pageable);
    }

    /**
     * 상태별 승인된 기도요청 조회
     */
    public Page<PrayerRequest> getPrayerRequestsByStatus(
        PrayerStatus status,
        Pageable pageable
    ) {
        return prayerRequestRepository.findByStatusAndIsApprovedTrueOrderByCreatedAtDesc(
            status, pageable
        );
    }

    /**
     * 기도 중인 요청 목록
     */
    public List<PrayerRequest> getPrayingRequests() {
        return prayerRequestRepository.findByStatusAndIsApprovedTrueOrderByCreatedAtDesc(
            PrayerStatus.PRAYING
        );
    }

    /**
     * 응답받은 기도요청 목록
     */
    public Page<PrayerRequest> getAnsweredPrayerRequests(Pageable pageable) {
        return prayerRequestRepository.findByStatusAndIsApprovedTrueOrderByAnsweredAtDesc(
            PrayerStatus.ANSWERED, pageable
        );
    }

    /**
     * 승인 대기 중인 기도요청 목록
     */
    public Page<PrayerRequest> getPendingPrayerRequests(Pageable pageable) {
        return prayerRequestRepository.findByIsApprovedFalseOrderByCreatedAtDesc(pageable);
    }

    /**
     * 기도요청 ID로 조회
     */
    public PrayerRequest getPrayerRequestById(Long id) {
        return prayerRequestRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("기도요청을 찾을 수 없습니다: " + id));
    }

    /**
     * 최신 기도요청 N개 조회
     */
    public List<PrayerRequest> getLatestPrayerRequests() {
        return prayerRequestRepository.findTop10ByIsApprovedTrueOrderByCreatedAtDesc();
    }

    /**
     * 제목으로 검색
     */
    public Page<PrayerRequest> searchByTitle(String keyword, Pageable pageable) {
        return prayerRequestRepository.findByTitleContainingAndIsApprovedTrue(
            keyword, pageable
        );
    }

    /**
     * 기도요청 생성
     */
    @Transactional
    public PrayerRequest createPrayerRequest(PrayerRequest prayerRequest) {
        // 기본적으로 승인 대기 상태
        prayerRequest.setIsApproved(false);
        prayerRequest.setStatus(PrayerStatus.PENDING);
        return prayerRequestRepository.save(prayerRequest);
    }

    /**
     * 기도요청 승인
     */
    @Transactional
    public PrayerRequest approvePrayerRequest(Long id) {
        PrayerRequest prayerRequest = getPrayerRequestById(id);

        prayerRequest.setIsApproved(true);
        prayerRequest.setStatus(PrayerStatus.PRAYING);

        return prayerRequestRepository.save(prayerRequest);
    }

    /**
     * 기도수 증가
     */
    @Transactional
    public void incrementPrayerCount(Long id) {
        prayerRequestRepository.incrementPrayerCount(id);
    }

    /**
     * 기도 응답으로 상태 변경
     */
    @Transactional
    public PrayerRequest markAsAnswered(Long id) {
        PrayerRequest prayerRequest = getPrayerRequestById(id);

        prayerRequest.setStatus(PrayerStatus.ANSWERED);
        prayerRequest.setAnsweredAt(LocalDateTime.now());

        return prayerRequestRepository.save(prayerRequest);
    }

    /**
     * 기도요청 수정
     */
    @Transactional
    public PrayerRequest updatePrayerRequest(Long id, PrayerRequest updatedRequest) {
        PrayerRequest prayerRequest = getPrayerRequestById(id);

        prayerRequest.setTitle(updatedRequest.getTitle());
        prayerRequest.setContent(updatedRequest.getContent());
        prayerRequest.setIsAnonymous(updatedRequest.getIsAnonymous());

        return prayerRequestRepository.save(prayerRequest);
    }

    /**
     * 기도요청 삭제
     */
    @Transactional
    public void deletePrayerRequest(Long id) {
        if (!prayerRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("기도요청을 찾을 수 없습니다: " + id);
        }
        prayerRequestRepository.deleteById(id);
    }

    /**
     * 통계: 상태별 기도요청 개수
     */
    public long getPrayerRequestCountByStatus(PrayerStatus status) {
        return prayerRequestRepository.countByStatusAndIsApproved(status, true);
    }

    /**
     * 통계: 승인 대기 중인 기도요청 개수
     */
    public long getPendingPrayerRequestCount() {
        return prayerRequestRepository.countByIsApprovedFalse();
    }
}
