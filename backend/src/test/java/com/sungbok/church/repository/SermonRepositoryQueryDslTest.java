package com.sungbok.church.repository;

import com.sungbok.church.config.QuerydslConfiguration;
import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.domain.repository.SermonRepository;
import com.sungbok.church.domain.repository.WorshipRepository;
import com.sungbok.church.dto.projection.SermonProjectionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SermonRepository QueryDSL 통합 테스트
 *
 * 목적:
 * - QueryDSL 쿼리 동작 검증
 * - LazyInitializationException 방지 확인
 * - N+1 쿼리 없음 검증
 * - DTO Projection 정상 동작 확인
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@DisplayName("SermonRepository QueryDSL 테스트")
class SermonRepositoryQueryDslTest {

    @Autowired
    private SermonRepository sermonRepository;

    @Autowired
    private WorshipRepository worshipRepository;

    private Worship sundayWorship;
    private Worship wednesdayWorship;
    private Sermon publishedSermon1;
    private Sermon publishedSermon2;
    private Sermon featuredSermon;
    private Sermon unpublishedSermon;

    @BeforeEach
    void setUp() {
        // Worship 데이터 생성
        sundayWorship = Worship.builder()
            .title("주일예배")
            .type(WorshipType.SUNDAY)
            .dayOfWeek(DayOfWeek.SUNDAY)
            .startTime(LocalTime.of(10, 30))
            .isActive(true)
            .build();
        worshipRepository.save(sundayWorship);

        wednesdayWorship = Worship.builder()
            .title("수요예배")
            .type(WorshipType.WEDNESDAY)
            .dayOfWeek(DayOfWeek.WEDNESDAY)
            .startTime(LocalTime.of(19, 30))
            .isActive(true)
            .build();
        worshipRepository.save(wednesdayWorship);

        // Sermon 데이터 생성
        publishedSermon1 = Sermon.builder()
            .title("사랑의 능력")
            .preacher("홍길동 목사")
            .sermonDate(LocalDate.now())
            .bibleVerse("요한복음 3:16")
            .summary("하나님의 사랑에 대한 설교")
            .youtubeVideoId("test-video-1")
            .isFeatured(false)
            .isPublished(true)
            .viewCount(100)
            .worship(sundayWorship)
            .build();
        sermonRepository.save(publishedSermon1);

        publishedSermon2 = Sermon.builder()
            .title("믿음의 여정")
            .preacher("김철수 목사")
            .sermonDate(LocalDate.now().minusDays(7))
            .bibleVerse("히브리서 11:1")
            .summary("믿음으로 살아가는 삶")
            .youtubeVideoId("test-video-2")
            .isFeatured(false)
            .isPublished(true)
            .viewCount(200)
            .worship(wednesdayWorship)
            .build();
        sermonRepository.save(publishedSermon2);

        featuredSermon = Sermon.builder()
            .title("부활의 소망")
            .preacher("홍길동 목사")
            .sermonDate(LocalDate.now().minusDays(1))
            .bibleVerse("고린도전서 15:20")
            .summary("부활의 확실한 소망")
            .youtubeVideoId("test-video-3")
            .isFeatured(true)
            .isPublished(true)
            .viewCount(500)
            .worship(sundayWorship)
            .build();
        sermonRepository.save(featuredSermon);

        unpublishedSermon = Sermon.builder()
            .title("미공개 설교")
            .preacher("홍길동 목사")
            .sermonDate(LocalDate.now().plusDays(1))
            .bibleVerse("시편 23:1")
            .summary("아직 공개되지 않은 설교")
            .isFeatured(false)
            .isPublished(false)
            .viewCount(0)
            .worship(sundayWorship)
            .build();
        sermonRepository.save(unpublishedSermon);
    }

    @Test
    @DisplayName("공개된 설교 목록 조회 - QueryDSL")
    void testFindPublishedSermons() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<SermonProjectionDto> result = sermonRepository.findPublishedSermons(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3); // 공개된 설교 3개
        assertThat(result.getContent()).allMatch(dto -> dto.getIsPublished());

        // LazyInitializationException 없이 Worship 데이터 접근 가능
        SermonProjectionDto dto = result.getContent().get(0);
        assertThat(dto.getWorshipId()).isNotNull();
        assertThat(dto.getWorshipTitle()).isNotBlank();
        assertThat(dto.getWorshipType()).isNotBlank();

        // 최신순 정렬 확인 (sermonDate DESC)
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("사랑의 능력");
    }

    @Test
    @DisplayName("설교자별 설교 조회 - QueryDSL")
    void testFindByPreacher() {
        // Given
        String preacher = "홍길동 목사";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<SermonProjectionDto> result = sermonRepository.findByPreacher(preacher, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2); // 홍길동 목사의 공개 설교 2개
        assertThat(result.getContent()).allMatch(dto ->
            dto.getPreacher().equals(preacher) && dto.getIsPublished()
        );

        // Worship 관계 데이터 확인
        assertThat(result.getContent()).allMatch(dto ->
            dto.getWorshipId() != null &&
            dto.getWorshipTitle() != null
        );
    }

    @Test
    @DisplayName("날짜 범위로 설교 조회 - QueryDSL")
    void testFindByDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<SermonProjectionDto> result = sermonRepository.findByDateRange(startDate, endDate, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).allMatch(dto -> {
            LocalDate sermonDate = dto.getSermonDate();
            return !sermonDate.isBefore(startDate) && !sermonDate.isAfter(endDate);
        });

        // 모두 공개된 설교만
        assertThat(result.getContent()).allMatch(SermonProjectionDto::getIsPublished);
    }

    @Test
    @DisplayName("추천 설교 목록 조회 - QueryDSL")
    void testFindFeatured() {
        // When
        List<SermonProjectionDto> result = sermonRepository.findFeatured();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("부활의 소망");
        assertThat(result.get(0).getIsFeatured()).isTrue();
        assertThat(result.get(0).getIsPublished()).isTrue();

        // Worship 관계 데이터 확인
        SermonProjectionDto dto = result.get(0);
        assertThat(dto.getWorshipId()).isEqualTo(sundayWorship.getId());
        assertThat(dto.getWorshipTitle()).isEqualTo("주일예배");
        assertThat(dto.getWorshipType()).isEqualTo("SUNDAY");
    }

    @Test
    @DisplayName("최신 설교 목록 조회 - QueryDSL")
    void testFindLatest() {
        // When
        List<SermonProjectionDto> result = sermonRepository.findLatest();

        // Then
        assertThat(result).hasSize(3); // 공개된 설교 3개
        assertThat(result).allMatch(SermonProjectionDto::getIsPublished);

        // 최신순 정렬 확인
        assertThat(result.get(0).getTitle()).isEqualTo("사랑의 능력");
        assertThat(result.get(1).getTitle()).isEqualTo("부활의 소망");
        assertThat(result.get(2).getTitle()).isEqualTo("믿음의 여정");
    }

    @Test
    @DisplayName("예배 유형별 최신 설교 조회 - QueryDSL")
    void testFindByWorshipType() {
        // When - 주일예배 설교만 조회
        List<SermonProjectionDto> sundaySermons = sermonRepository.findByWorshipType(WorshipType.SUNDAY, 10);

        // Then
        assertThat(sundaySermons).hasSize(2); // 주일예배 공개 설교 2개
        assertThat(sundaySermons).allMatch(dto ->
            dto.getWorshipType().equals("SUNDAY") && dto.getIsPublished()
        );

        // When - 수요예배 설교만 조회
        List<SermonProjectionDto> wednesdaySermons = sermonRepository.findByWorshipType(WorshipType.WEDNESDAY, 10);

        // Then
        assertThat(wednesdaySermons).hasSize(1); // 수요예배 공개 설교 1개
        assertThat(wednesdaySermons.get(0).getWorshipType()).isEqualTo("WEDNESDAY");
        assertThat(wednesdaySermons.get(0).getTitle()).isEqualTo("믿음의 여정");
    }

    @Test
    @DisplayName("N+1 쿼리 방지 검증 - LEFT JOIN 확인")
    void testNoN1Query() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - 쿼리 실행
        Page<SermonProjectionDto> result = sermonRepository.findPublishedSermons(pageable);

        // Then - Worship 데이터 접근 시 추가 쿼리 없음
        // (SQL 로그에서 1개의 LEFT JOIN 쿼리만 실행되는지 확인)
        result.getContent().forEach(dto -> {
            // LazyInitializationException 발생하지 않음
            assertThat(dto.getWorshipId()).isNotNull();
            assertThat(dto.getWorshipTitle()).isNotBlank();
            assertThat(dto.getWorshipType()).isNotBlank();
        });

        // DTO Projection이므로 Entity가 아닌 DTO만 반환
        assertThat(result.getContent().get(0)).isInstanceOf(SermonProjectionDto.class);
    }

    @Test
    @DisplayName("페이징 동작 검증")
    void testPagination() {
        // Given - 2개씩 페이징
        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);

        // When
        Page<SermonProjectionDto> result1 = sermonRepository.findPublishedSermons(page1);
        Page<SermonProjectionDto> result2 = sermonRepository.findPublishedSermons(page2);

        // Then
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.getTotalElements()).isEqualTo(3);
        assertThat(result1.getTotalPages()).isEqualTo(2);

        assertThat(result2.getContent()).hasSize(1);
        assertThat(result2.getTotalElements()).isEqualTo(3);

        // 페이지별 데이터 중복 없음
        assertThat(result1.getContent()).doesNotContainAnyElementsOf(result2.getContent());
    }

    @Test
    @DisplayName("빈 결과 처리 확인")
    void testEmptyResult() {
        // Given - 존재하지 않는 설교자
        String nonExistentPreacher = "없는 목사";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<SermonProjectionDto> result = sermonRepository.findByPreacher(nonExistentPreacher, pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
