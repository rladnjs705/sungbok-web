package com.sungbok.church.service;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.exception.ResourceNotFoundException;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.domain.repository.WorshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * WorshipService Unit Test
 *
 * Testing Strategy:
 * 1. Repository 메서드 호출 검증
 * 2. 비즈니스 로직 검증 (예배 중복 체크, 라이브 상태 토글)
 * 3. 예외 처리 검증
 * 4. 삭제 전 존재 확인 검증 (Phase 2/3 개선사항)
 * 5. 라이브 상태 관리 검증 (상호 배타적)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WorshipService 단위 테스트")
class WorshipServiceTest {

    @Mock
    private WorshipRepository worshipRepository;

    @InjectMocks
    private WorshipService worshipService;

    private Worship testWorship;
    private Worship anotherWorship;

    @BeforeEach
    void setUp() {
        testWorship = Worship.builder()
                .title("주일 예배")
                .type(WorshipType.SUNDAY)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(LocalTime.of(11, 0))
                .location("본당")
                .description("주일 대예배")
                .liveStreamUrl("https://youtube.com/live/abc123")
                .isLiveNow(false)
                .isActive(true)
                .build();

        anotherWorship = Worship.builder()
                .title("수요 예배")
                .type(WorshipType.WEDNESDAY)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(19, 30))
                .location("본당")
                .description("수요 저녁 예배")
                .liveStreamUrl("https://youtube.com/live/xyz789")
                .isLiveNow(true)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("활성화된 예배 목록 조회")
    void getActiveWorships_Success() {
        // given
        given(worshipRepository.findByIsActiveTrueOrderByDayOfWeekAsc())
                .willReturn(List.of(testWorship, anotherWorship));

        // when
        List<Worship> result = worshipService.getActiveWorships();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(worshipRepository, times(1)).findByIsActiveTrueOrderByDayOfWeekAsc();
    }

    @Test
    @DisplayName("예배 유형별 조회")
    void getWorshipsByType_Success() {
        // given
        given(worshipRepository.findByTypeAndIsActiveTrue(WorshipType.SUNDAY))
                .willReturn(List.of(testWorship));

        // when
        List<Worship> result = worshipService.getWorshipsByType(WorshipType.SUNDAY);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(WorshipType.SUNDAY);
        verify(worshipRepository, times(1)).findByTypeAndIsActiveTrue(WorshipType.SUNDAY);
    }

    @Test
    @DisplayName("요일별 예배 조회")
    void getWorshipsByDayOfWeek_Success() {
        // given
        given(worshipRepository.findByDayOfWeekAndIsActiveTrue(DayOfWeek.SUNDAY))
                .willReturn(List.of(testWorship));

        // when
        List<Worship> result = worshipService.getWorshipsByDayOfWeek(DayOfWeek.SUNDAY);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
        verify(worshipRepository, times(1)).findByDayOfWeekAndIsActiveTrue(DayOfWeek.SUNDAY);
    }

    @Test
    @DisplayName("현재 라이브 중인 예배 조회")
    void getLiveWorship_Success() {
        // given
        given(worshipRepository.findByIsLiveNowTrue())
                .willReturn(Optional.of(anotherWorship));

        // when
        Optional<Worship> result = worshipService.getLiveWorship();

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getIsLiveNow()).isTrue();
        assertThat(result.get().getTitle()).isEqualTo("수요 예배");
        verify(worshipRepository, times(1)).findByIsLiveNowTrue();
    }

    @Test
    @DisplayName("현재 라이브 중인 예배 없음")
    void getLiveWorship_NotFound() {
        // given
        given(worshipRepository.findByIsLiveNowTrue())
                .willReturn(Optional.empty());

        // when
        Optional<Worship> result = worshipService.getLiveWorship();

        // then
        assertThat(result).isEmpty();
        verify(worshipRepository, times(1)).findByIsLiveNowTrue();
    }

    @Test
    @DisplayName("라이브 스트리밍 예배 목록 조회")
    void getLiveStreamWorships_Success() {
        // given
        given(worshipRepository.findByLiveStreamUrlIsNotNullAndIsActiveTrue())
                .willReturn(List.of(testWorship, anotherWorship));

        // when
        List<Worship> result = worshipService.getLiveStreamWorships();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLiveStreamUrl()).isNotNull();
        verify(worshipRepository, times(1)).findByLiveStreamUrlIsNotNullAndIsActiveTrue();
    }

    @Test
    @DisplayName("예배 생성 성공")
    void createWorship_Success() {
        // given
        given(worshipRepository.existsByTypeAndDayOfWeek(
                testWorship.getType(), testWorship.getDayOfWeek()))
                .willReturn(false);
        given(worshipRepository.save(testWorship))
                .willReturn(testWorship);

        // when
        Worship result = worshipService.createWorship(testWorship);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("주일 예배");
        verify(worshipRepository, times(1)).existsByTypeAndDayOfWeek(
                WorshipType.SUNDAY, DayOfWeek.SUNDAY);
        verify(worshipRepository, times(1)).save(testWorship);
    }

    @Test
    @DisplayName("예배 생성 실패 - 중복된 예배 (Type + DayOfWeek)")
    void createWorship_Failure_Duplicate() {
        // given
        given(worshipRepository.existsByTypeAndDayOfWeek(
                testWorship.getType(), testWorship.getDayOfWeek()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> worshipService.createWorship(testWorship))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("이미 존재하는 예배입니다");

        verify(worshipRepository, times(1)).existsByTypeAndDayOfWeek(
                WorshipType.SUNDAY, DayOfWeek.SUNDAY);
        verify(worshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("예배 수정 성공")
    void updateWorship_Success() {
        // given
        Long worshipId = 1L;
        Worship updatedWorship = Worship.builder()
                .title("수정된 주일 예배")
                .type(WorshipType.SUNDAY)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(LocalTime.of(10, 30))
                .location("대예배실")
                .description("수정된 설명")
                .liveStreamUrl("https://youtube.com/live/updated")
                .isActive(true)
                .build();

        given(worshipRepository.findById(worshipId))
                .willReturn(Optional.of(testWorship));
        given(worshipRepository.save(testWorship))
                .willReturn(testWorship);

        // when
        Worship result = worshipService.updateWorship(worshipId, updatedWorship);

        // then
        assertThat(result).isNotNull();
        verify(worshipRepository, times(1)).findById(worshipId);
        verify(worshipRepository, times(1)).save(testWorship);
    }

    @Test
    @DisplayName("예배 수정 실패 - 존재하지 않는 ID")
    void updateWorship_NotFound_ThrowsException() {
        // given
        Long worshipId = 999L;
        given(worshipRepository.findById(worshipId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> worshipService.updateWorship(worshipId, testWorship))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("예배를 찾을 수 없습니다");

        verify(worshipRepository, times(1)).findById(worshipId);
        verify(worshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("라이브 상태 토글 - 활성화 (다른 라이브 예배 비활성화)")
    void toggleLiveStatus_Activate_Success() {
        // given
        Long worshipId = 1L;

        // 현재 라이브 중인 다른 예배가 있음
        given(worshipRepository.findById(worshipId))
                .willReturn(Optional.of(testWorship));
        given(worshipRepository.findByIsLiveNowTrue())
                .willReturn(Optional.of(anotherWorship));
        given(worshipRepository.save(any(Worship.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        worshipService.toggleLiveStatus(worshipId, true);

        // then
        verify(worshipRepository, times(1)).findById(worshipId);
        verify(worshipRepository, times(1)).findByIsLiveNowTrue();
        verify(worshipRepository, times(2)).save(any(Worship.class)); // anotherWorship + testWorship
    }

    @Test
    @DisplayName("라이브 상태 토글 - 비활성화")
    void toggleLiveStatus_Deactivate_Success() {
        // given
        Long worshipId = 1L;

        given(worshipRepository.findById(worshipId))
                .willReturn(Optional.of(testWorship));
        given(worshipRepository.save(testWorship))
                .willReturn(testWorship);

        // when
        worshipService.toggleLiveStatus(worshipId, false);

        // then
        verify(worshipRepository, times(1)).findById(worshipId);
        verify(worshipRepository, never()).findByIsLiveNowTrue(); // 비활성화 시에는 다른 예배 찾지 않음
        verify(worshipRepository, times(1)).save(testWorship);
    }

    @Test
    @DisplayName("라이브 상태 토글 - 활성화 (다른 라이브 예배 없음)")
    void toggleLiveStatus_Activate_NoOtherLive() {
        // given
        Long worshipId = 1L;

        given(worshipRepository.findById(worshipId))
                .willReturn(Optional.of(testWorship));
        given(worshipRepository.findByIsLiveNowTrue())
                .willReturn(Optional.empty());
        given(worshipRepository.save(testWorship))
                .willReturn(testWorship);

        // when
        worshipService.toggleLiveStatus(worshipId, true);

        // then
        verify(worshipRepository, times(1)).findById(worshipId);
        verify(worshipRepository, times(1)).findByIsLiveNowTrue();
        verify(worshipRepository, times(1)).save(testWorship);
    }

    @Test
    @DisplayName("예배 삭제 성공 - Phase 2/3 개선사항: 존재 확인 검증")
    void deleteWorship_Success_WithExistenceCheck() {
        // given
        Long worshipId = 1L;
        given(worshipRepository.existsById(worshipId))
                .willReturn(true);
        doNothing().when(worshipRepository).deleteById(worshipId);

        // when
        worshipService.deleteWorship(worshipId);

        // then
        verify(worshipRepository, times(1)).existsById(worshipId);
        verify(worshipRepository, times(1)).deleteById(worshipId);
    }

    @Test
    @DisplayName("예배 삭제 실패 - 존재하지 않는 ID (Phase 2/3 개선사항 검증)")
    void deleteWorship_NotFound_ThrowsException() {
        // given
        Long worshipId = 999L;
        given(worshipRepository.existsById(worshipId))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> worshipService.deleteWorship(worshipId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("예배를 찾을 수 없습니다");

        verify(worshipRepository, times(1)).existsById(worshipId);
        verify(worshipRepository, never()).deleteById(anyLong());
    }
}
