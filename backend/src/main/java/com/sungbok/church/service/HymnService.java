package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Hymn;
import com.sungbok.church.domain.repository.HymnRepository;
import com.sungbok.church.dto.projection.HymnProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hymn Service
 * 찬양 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HymnService {

    private final HymnRepository hymnRepository;

    /**
     * 찬양 번호로 조회
     */
    public Hymn getHymnByNumber(Integer hymnNumber) {
        return hymnRepository.findByHymnNumber(hymnNumber)
            .orElseThrow(() -> new IllegalArgumentException("찬양을 찾을 수 없습니다: " + hymnNumber));
    }

    /**
     * 제목으로 검색
     */
    public Page<Hymn> searchByTitle(String keyword, Pageable pageable) {
        return hymnRepository.findByTitleContaining(keyword, pageable);
    }

    /**
     * 가사로 검색
     */
    public Page<Hymn> searchByLyrics(String keyword, Pageable pageable) {
        return hymnRepository.findByLyricsContaining(keyword, pageable);
    }

    /**
     * 제목 또는 가사로 검색
     */
    public Page<HymnProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        return hymnRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 많이 연주된 찬양 조회
     */
    public List<Hymn> getPopularHymns() {
        return hymnRepository.findTop20ByOrderByPerformanceCountDesc();
    }

    /**
     * 최근 추가된 찬양 조회
     */
    public List<Hymn> getRecentHymns() {
        return hymnRepository.findTop20ByOrderByCreatedAtDesc();
    }

    /**
     * 찬양 ID로 조회
     */
    public Hymn getHymnById(Long id) {
        return hymnRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("찬양을 찾을 수 없습니다: " + id));
    }

    /**
     * 찬양 생성
     */
    @Transactional
    public Hymn createHymn(Hymn hymn) {
        validateHymnNumberUniqueness(hymn.getHymnNumber());
        return hymnRepository.save(hymn);
    }

    /**
     * 연주 횟수 증가
     */
    @Transactional
    public void incrementPerformanceCount(Long id) {
        hymnRepository.incrementPerformanceCount(id);
    }

    /**
     * 찬양 수정
     */
    @Transactional
    public Hymn updateHymn(Long id, Hymn updatedHymn) {
        Hymn hymn = getHymnById(id);

        if (!hymn.getHymnNumber().equals(updatedHymn.getHymnNumber())) {
            validateHymnNumberUniqueness(updatedHymn.getHymnNumber());
            hymn.setHymnNumber(updatedHymn.getHymnNumber());
        }

        hymn.setTitle(updatedHymn.getTitle());
        hymn.setComposer(updatedHymn.getComposer());
        hymn.setLyricist(updatedHymn.getLyricist());
        hymn.setLyrics(updatedHymn.getLyrics());
        hymn.setYoutubeUrl(updatedHymn.getYoutubeUrl());
        hymn.setSheetMusicUrl(updatedHymn.getSheetMusicUrl());

        return hymnRepository.save(hymn);
    }

    /**
     * 찬양 삭제
     */
    @Transactional
    public void deleteHymn(Long id) {
        if (!hymnRepository.existsById(id)) {
            throw new IllegalArgumentException("찬양을 찾을 수 없습니다: " + id);
        }
        hymnRepository.deleteById(id);
    }

    /**
     * 찬양 번호 중복 체크
     */
    private void validateHymnNumberUniqueness(Integer hymnNumber) {
        if (hymnRepository.existsByHymnNumber(hymnNumber)) {
            throw new IllegalArgumentException("이미 존재하는 찬양 번호입니다: " + hymnNumber);
        }
    }
}
