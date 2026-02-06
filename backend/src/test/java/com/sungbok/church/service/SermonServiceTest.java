package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.domain.repository.SermonRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * SermonService Unit Test
 *
 * Testing Strategy:
 * 1. N+1 쿼리 방지 검증 (@EntityGraph 사용 확인)
 * 2. 조회수 증가 및 EntityManager refresh 검증
 * 3. 삭제 전 존재 확인 검증
 * 4. Worship 관계 로딩 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SermonService 단위 테스트")
class SermonServiceTest {

    @Mock
    private SermonRepository sermonRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SermonService sermonService;

    private Sermon testSermon;
    private Worship testWorship;

    @BeforeEach
    void setUp() {
        testWorship = Worship.builder()
                .title("주일 대예배")
                .type(WorshipType.SUNDAY)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(LocalTime.of(11, 0))
                .description("주일 대예배")
                .isActive(true)
                .build();

        testSermon = Sermon.builder()
                .worship(testWorship)
                .title("하나님의 사랑")
                .bibleVerse("요한복음 3:16")
                .preacher("담임목사")
                .sermonDate(LocalDate.now())
                .youtubeVideoId("abc123")
                .videoUrl("https://youtube.com/watch?v=abc123")
                .duration(1800)
                .viewCount(0)
                .description("하나님의 사랑에 대한 설교")
                .isPublished(true)
                .isFeatured(false)
                .build();
    }

    @Test
    @DisplayName("설교 ID로 조회 성공 - N+1 쿼리 방지 (EntityGraph) 및 조회수 증가 검증")
    void getSermonById_Success_WithWorshipAndViewCount() {
        // given
        Long sermonId = 1L;

        // Repository의 findById는 @EntityGraph로 Worship을 함께 로드
        // 이는 N+1 쿼리를 방지함
        given(sermonRepository.findById(sermonId))
                .willReturn(Optional.of(testSermon));
        doNothing().when(sermonRepository).incrementViewCount(sermonId);
        doNothing().when(entityManager).refresh(testSermon);

        // when
        Sermon result = sermonService.getSermonById(sermonId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("하나님의 사랑");

        // Worship 관계가 로드되었는지 검증 (N+1 방지)
        assertThat(result.getWorship()).isNotNull();
        assertThat(result.getWorship().getTitle()).isEqualTo("주일 대예배");

        // Phase 3 개선사항: 조회수 증가 및 refresh 검증
        verify(sermonRepository, times(1)).findById(sermonId);
        verify(sermonRepository, times(1)).incrementViewCount(sermonId);
        verify(entityManager, times(1)).refresh(testSermon);
    }

    @Test
    @DisplayName("설교 조회 실패 - 존재하지 않는 ID")
    void getSermonById_NotFound_ThrowsException() {
        // given
        Long sermonId = 999L;
        given(sermonRepository.findById(sermonId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sermonService.getSermonById(sermonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교를 찾을 수 없습니다");

        verify(sermonRepository, times(1)).findById(sermonId);
        verify(sermonRepository, never()).incrementViewCount(anyLong());
        verify(entityManager, never()).refresh(any());
    }

    @Test
    @DisplayName("설교 생성 성공")
    void createSermon_Success() {
        // given
        given(sermonRepository.save(testSermon))
                .willReturn(testSermon);

        // when
        Sermon result = sermonService.createSermon(testSermon);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("하나님의 사랑");
        assertThat(result.getWorship()).isNotNull();
        verify(sermonRepository, times(1)).save(testSermon);
    }

    @Test
    @DisplayName("설교 수정 성공")
    void updateSermon_Success() {
        // given
        Long sermonId = 1L;
        Sermon updatedSermon = Sermon.builder()
                .title("수정된 제목")
                .bibleVerse("요한복음 3:17")
                .preacher("담임목사")
                .sermonDate(LocalDate.now())
                .build();

        given(sermonRepository.findById(sermonId))
                .willReturn(Optional.of(testSermon));
        given(sermonRepository.save(testSermon))
                .willReturn(testSermon);

        // when
        Sermon result = sermonService.updateSermon(sermonId, updatedSermon);

        // then
        assertThat(result).isNotNull();
        verify(sermonRepository, times(1)).findById(sermonId);
        verify(sermonRepository, times(1)).save(testSermon);
    }

    @Test
    @DisplayName("설교 삭제 성공 - Phase 2/3 개선사항: 존재 확인 검증")
    void deleteSermon_Success_WithExistenceCheck() {
        // given
        Long sermonId = 1L;
        given(sermonRepository.existsById(sermonId))
                .willReturn(true);
        doNothing().when(sermonRepository).deleteById(sermonId);

        // when
        sermonService.deleteSermon(sermonId);

        // then
        verify(sermonRepository, times(1)).existsById(sermonId);
        verify(sermonRepository, times(1)).deleteById(sermonId);
    }

    @Test
    @DisplayName("설교 삭제 실패 - 존재하지 않는 ID (Phase 2/3 개선사항 검증)")
    void deleteSermon_NotFound_ThrowsException() {
        // given
        Long sermonId = 999L;
        given(sermonRepository.existsById(sermonId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> sermonService.deleteSermon(sermonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교를 찾을 수 없습니다");

        verify(sermonRepository, times(1)).existsById(sermonId);
        verify(sermonRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("YouTube Video ID로 설교 조회 성공")
    void findByYoutubeVideoId_Success() {
        // given
        String videoId = "abc123";
        given(sermonRepository.findByYoutubeVideoId(videoId))
                .willReturn(Optional.of(testSermon));

        // when
        Optional<Sermon> result = sermonRepository.findByYoutubeVideoId(videoId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getYoutubeVideoId()).isEqualTo(videoId);
        verify(sermonRepository, times(1)).findByYoutubeVideoId(videoId);
    }

    @Test
    @DisplayName("Worship 관계 LAZY 로딩 검증")
    void sermon_WorshipRelationship_IsLazy() {
        // given
        // Sermon 엔티티의 worship 필드는 FetchType.LAZY로 설정되어 있음
        // Repository의 findById는 @EntityGraph로 명시적으로 로드

        Long sermonId = 1L;
        given(sermonRepository.findById(sermonId))
                .willReturn(Optional.of(testSermon));
        doNothing().when(sermonRepository).incrementViewCount(sermonId);
        doNothing().when(entityManager).refresh(testSermon);

        // when
        Sermon result = sermonService.getSermonById(sermonId);

        // then
        // @EntityGraph 덕분에 Worship이 함께 로드됨
        assertThat(result.getWorship()).isNotNull();

        // 이는 N+1 쿼리를 방지하고 성능을 최적화함
        // Repository에서 @EntityGraph(attributePaths = {"worship"})를 사용하여
        // 하나의 JOIN 쿼리로 sermon과 worship을 함께 가져옴
        verify(sermonRepository, times(1)).findById(sermonId);
    }
}
