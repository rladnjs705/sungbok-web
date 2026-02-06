package com.sungbok.church.service;

import com.sungbok.church.domain.repository.StaffRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffService 단위 테스트")
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    @Test
    @DisplayName("교역자 삭제 - 존재 확인 검증")
    void deleteStaff_Success_WithExistenceCheck() {
        // given
        Long id = 1L;
        given(staffRepository.existsById(id)).willReturn(true);

        // when
        staffService.deleteStaff(id);

        // then
        verify(staffRepository, times(1)).existsById(id);
        verify(staffRepository, times(1)).deleteById(id);
    }
}
