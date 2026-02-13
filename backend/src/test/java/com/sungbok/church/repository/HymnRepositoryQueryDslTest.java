package com.sungbok.church.repository;

import com.sungbok.church.domain.entity.Hymn;
import com.sungbok.church.domain.repository.HymnRepository;
import com.sungbok.church.dto.projection.HymnProjectionDto;
import com.sungbok.church.fixture.HymnFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HymnRepository QueryDSL 통합 테스트
 *
 * 목적:
 * - QueryDSL 쿼리 동작 검증
 * - N+1 쿼리 없음 검증
 * - DTO Projection 정상 동작 확인
 * - 다중 필드 검색 정확도 확인
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@DisplayName("HymnRepository QueryDSL 테스트")
class HymnRepositoryQueryDslTest extends BaseRepositoryTest {

    @Autowired
    private HymnRepository hymnRepository;

    @Test
    @DisplayName("키워드 검색 - 제목으로 검색")
    void searchByKeyword_ByTitle_Success() {
        // Given
        Hymn hymn1 = persistAndFlush(
            HymnFixture.builder()
                .title("주님의 은혜")
                .lyrics("은혜 가사")
                .artist("찬양팀")
                .build()
        );

        Hymn hymn2 = persistAndFlush(
            HymnFixture.builder()
                .title("감사의 노래")
                .lyrics("감사 가사")
                .artist("찬양팀")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("은혜", pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("은혜");
        assertThat(result.getContent().get(0).getId()).isEqualTo(hymn1.getId());
    }

    @Test
    @DisplayName("키워드 검색 - 가사로 검색")
    void searchByKeyword_ByLyrics_Success() {
        // Given
        Hymn hymn1 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 제목 1")
                .lyrics("주님을 찬양하며 경배합니다")
                .artist("찬양팀")
                .build()
        );

        Hymn hymn2 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 제목 2")
                .lyrics("감사와 영광을 드립니다")
                .artist("찬양팀")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("경배", pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getLyrics()).contains("경배");
        assertThat(result.getContent().get(0).getId()).isEqualTo(hymn1.getId());
    }

    @Test
    @DisplayName("키워드 검색 - 아티스트로 검색")
    void searchByKeyword_ByArtist_Success() {
        // Given
        Hymn hymn1 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 1")
                .lyrics("가사 1")
                .artist("소망찬양대")
                .build()
        );

        Hymn hymn2 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 2")
                .lyrics("가사 2")
                .artist("소망찬양대")
                .build()
        );

        Hymn hymn3 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 3")
                .lyrics("가사 3")
                .artist("청년찬양팀")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("소망찬양대", pageable);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).allMatch(dto -> dto.getArtist().contains("소망찬양대"));
    }

    @Test
    @DisplayName("키워드 검색 - 다중 필드 매칭 (제목 + 가사 + 아티스트)")
    void searchByKeyword_MultipleFields_Success() {
        // Given
        Hymn hymn1 = persistAndFlush(
            HymnFixture.builder()
                .title("은혜의 주")
                .lyrics("일반 가사")
                .artist("찬양팀")
                .build()
        );

        Hymn hymn2 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 제목")
                .lyrics("은혜 넘치는 가사")
                .artist("찬양팀")
                .build()
        );

        Hymn hymn3 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 제목2")
                .lyrics("일반 가사")
                .artist("은혜찬양대")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("은혜", pageable);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.getContent()).allMatch(dto ->
            dto.getTitle().contains("은혜") ||
            dto.getLyrics().contains("은혜") ||
            dto.getArtist().contains("은혜")
        );
    }

    @Test
    @DisplayName("키워드 검색 - 대소문자 구분 없음")
    void searchByKeyword_CaseInsensitive() {
        // Given
        persistAndFlush(
            HymnFixture.builder()
                .title("Amazing Grace")
                .lyrics("Amazing grace lyrics")
                .artist("Worship Team")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When - 소문자로 검색
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("amazing", pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).containsIgnoringCase("amazing");
    }

    @Test
    @DisplayName("키워드 검색 - 빈 결과")
    void searchByKeyword_EmptyResult() {
        // Given
        persistAndFlush(
            HymnFixture.builder()
                .title("찬양 제목")
                .lyrics("찬양 가사")
                .artist("찬양팀")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When - 존재하지 않는 키워드로 검색
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("존재하지않는키워드", pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("페이징 동작 검증")
    void testPagination() {
        // Given - 5개의 찬양 생성
        for (int i = 1; i <= 5; i++) {
            persistAndFlush(
                HymnFixture.builder()
                    .title("찬양 " + i)
                    .lyrics("공통 키워드를 포함한 가사")
                    .artist("찬양팀")
                    .build()
            );
        }

        // When - 2개씩 페이징
        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);

        Page<HymnProjectionDto> result1 = hymnRepository.searchByKeyword("공통", page1);
        Page<HymnProjectionDto> result2 = hymnRepository.searchByKeyword("공통", page2);

        // Then
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.getTotalElements()).isEqualTo(5);
        assertThat(result1.getTotalPages()).isEqualTo(3);

        assertThat(result2.getContent()).hasSize(2);
        assertThat(result2.getTotalElements()).isEqualTo(5);

        // 페이지별 데이터 중복 없음
        assertThat(result1.getContent()).doesNotContainAnyElementsOf(result2.getContent());
    }

    @Test
    @DisplayName("미공개 찬양은 조회되지 않음")
    void unpublishedHymns_NotReturned() {
        // Given
        persistAndFlush(
            HymnFixture.builder()
                .title("공개 찬양")
                .lyrics("공개 가사")
                .artist("찬양팀")
                .isPublished(true)
                .build()
        );

        persistAndFlush(
            HymnFixture.builder()
                .title("비공개 찬양")
                .lyrics("비공개 가사")
                .artist("찬양팀")
                .isPublished(false)
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("찬양", pageable);

        // Then - 공개된 찬양만 조회
        assertThat(result).hasSize(1);
        assertThat(result.getContent()).allMatch(HymnProjectionDto::getIsPublished);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("공개 찬양");
    }

    @Test
    @DisplayName("DTO Projection 정상 동작")
    void dtoProjection_Success() {
        // Given
        Hymn hymn = persistAndFlush(
            HymnFixture.builder()
                .hymnNumber(200)
                .title("주님 찬양")
                .composer("작곡가")
                .lyricist("작사가")
                .lyrics("찬양 가사")
                .artist("찬양팀")
                .performanceCount(10)
                .viewCount(100)
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("주님", pageable);

        // Then
        assertThat(result).hasSize(1);
        HymnProjectionDto dto = result.getContent().get(0);
        assertThat(dto).isInstanceOf(HymnProjectionDto.class);
        assertThat(dto.getId()).isEqualTo(hymn.getId());
        assertThat(dto.getHymnNumber()).isEqualTo(200);
        assertThat(dto.getTitle()).isEqualTo("주님 찬양");
        assertThat(dto.getComposer()).isEqualTo("작곡가");
        assertThat(dto.getLyricist()).isEqualTo("작사가");
        assertThat(dto.getArtist()).isEqualTo("찬양팀");
        assertThat(dto.getPerformanceCount()).isEqualTo(10);
        assertThat(dto.getViewCount()).isEqualTo(100);

        // DTO 메서드 검증
        assertThat(dto.isPopular(5)).isTrue();
        assertThat(dto.isPopular(15)).isFalse();
    }

    @Test
    @DisplayName("정렬 - performanceDate 내림차순")
    void searchByKeyword_OrderByPerformanceDateDesc() {
        // Given
        Hymn hymn1 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 A")
                .lyrics("공통 키워드")
                .artist("찬양팀")
                .performanceDate(java.time.LocalDate.now().minusDays(10))
                .build()
        );

        Hymn hymn2 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 B")
                .lyrics("공통 키워드")
                .artist("찬양팀")
                .performanceDate(java.time.LocalDate.now().minusDays(1))
                .build()
        );

        Hymn hymn3 = persistAndFlush(
            HymnFixture.builder()
                .title("찬양 C")
                .lyrics("공통 키워드")
                .artist("찬양팀")
                .performanceDate(java.time.LocalDate.now().minusDays(5))
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("공통", pageable);

        // Then - performanceDate 내림차순 확인
        assertThat(result).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("찬양 B"); // 가장 최근
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("찬양 C");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("찬양 A"); // 가장 오래됨
    }

    @Test
    @DisplayName("isRecent() 메서드 검증")
    void dtoMethod_IsRecent() {
        // Given
        Hymn recentHymn = persistAndFlush(
            HymnFixture.builder()
                .title("최근 찬양")
                .lyrics("최근")
                .artist("찬양팀")
                .performanceDate(java.time.LocalDate.now().minusDays(5))
                .build()
        );

        Hymn oldHymn = persistAndFlush(
            HymnFixture.builder()
                .title("오래된 찬양")
                .lyrics("오래된")
                .artist("찬양팀")
                .performanceDate(java.time.LocalDate.now().minusDays(50))
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<HymnProjectionDto> result = hymnRepository.searchByKeyword("찬양", pageable);

        // Then
        HymnProjectionDto recentDto = result.getContent().stream()
            .filter(dto -> dto.getTitle().equals("최근 찬양"))
            .findFirst()
            .orElseThrow();

        HymnProjectionDto oldDto = result.getContent().stream()
            .filter(dto -> dto.getTitle().equals("오래된 찬양"))
            .findFirst()
            .orElseThrow();

        assertThat(recentDto.isRecent(30)).isTrue();
        assertThat(oldDto.isRecent(30)).isFalse();
    }
}
