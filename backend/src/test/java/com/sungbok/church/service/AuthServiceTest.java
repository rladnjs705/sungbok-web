package com.sungbok.church.service;

import com.sungbok.church.api.dto.request.LoginRequest;
import com.sungbok.church.api.dto.request.RegisterRequest;
import com.sungbok.church.api.dto.response.AuthResponse;
import com.sungbok.church.domain.entity.User;
import com.sungbok.church.domain.enums.UserRole;
import com.sungbok.church.domain.repository.UserRepository;
import com.sungbok.church.exception.BusinessException;
import com.sungbok.church.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * AuthService Unit Test
 *
 * Testing Strategy:
 * 1. 로그인 성공/실패 시나리오
 * 2. 회원가입 성공/실패 시나리오
 * 3. JWT 토큰 생성 검증
 * 4. 중복 체크 검증
 * 5. 비밀번호 암호화 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .name("테스트 사용자")
                .email("test@example.com")
                .phone("010-1234-5678")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        mockAuthentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        LoginRequest request = new LoginRequest("testuser", "password123");
        String expectedToken = "jwt.token.here";

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mockAuthentication);
        given(tokenProvider.createToken(mockAuthentication))
                .willReturn(expectedToken);
        given(userRepository.findByUsername("testuser"))
                .willReturn(Optional.of(testUser));

        // when
        AuthResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USER");

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).createToken(mockAuthentication);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 크레덴셜")
    void login_Failure_BadCredentials() {
        // given
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, never()).createToken(any());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_Success() {
        // given
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "password123",
                "새 사용자",
                "new@example.com",
                "010-9999-8888"
        );
        String expectedToken = "jwt.token.here";

        given(userRepository.existsByUsername("newuser"))
                .willReturn(false);
        given(userRepository.existsByEmail("new@example.com"))
                .willReturn(false);
        given(passwordEncoder.encode("password123"))
                .willReturn("encodedPassword");
        given(userRepository.save(any(User.class)))
                .willReturn(testUser);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mockAuthentication);
        given(tokenProvider.createToken(mockAuthentication))
                .willReturn(expectedToken);

        // when
        AuthResponse response = authService.register(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getType()).isEqualTo("Bearer");

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).createToken(mockAuthentication);
    }

    @Test
    @DisplayName("회원가입 실패 - 사용자명 중복")
    void register_Failure_DuplicateUsername() {
        // given
        RegisterRequest request = new RegisterRequest(
                "existinguser",
                "password123",
                "새 사용자",
                "new@example.com",
                "010-9999-8888"
        );

        given(userRepository.existsByUsername("existinguser"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 사용자명입니다");

        verify(userRepository, times(1)).existsByUsername("existinguser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_Failure_DuplicateEmail() {
        // given
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "password123",
                "새 사용자",
                "existing@example.com",
                "010-9999-8888"
        );

        given(userRepository.existsByUsername("newuser"))
                .willReturn(false);
        given(userRepository.existsByEmail("existing@example.com"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다");

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원가입 성공 - 이메일 없이")
    void register_Success_WithoutEmail() {
        // given
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "password123",
                "새 사용자",
                null,
                "010-9999-8888"
        );
        String expectedToken = "jwt.token.here";

        given(userRepository.existsByUsername("newuser"))
                .willReturn(false);
        given(passwordEncoder.encode("password123"))
                .willReturn("encodedPassword");
        given(userRepository.save(any(User.class)))
                .willReturn(testUser);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mockAuthentication);
        given(tokenProvider.createToken(mockAuthentication))
                .willReturn(expectedToken);

        // when
        AuthResponse response = authService.register(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 암호화 검증")
    void register_PasswordEncryption() {
        // given
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "plainPassword",
                "새 사용자",
                null,
                null
        );

        given(userRepository.existsByUsername(anyString()))
                .willReturn(false);
        given(passwordEncoder.encode("plainPassword"))
                .willReturn("$2a$10$encodedPassword");
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(authenticationManager.authenticate(any()))
                .willReturn(mockAuthentication);
        given(tokenProvider.createToken(any()))
                .willReturn("token");

        // when
        authService.register(request);

        // then
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(argThat(user ->
                user.getPassword().equals("$2a$10$encodedPassword")
        ));
    }

    @Test
    @DisplayName("기본 역할은 USER로 설정")
    void register_DefaultRoleIsUser() {
        // given
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "password123",
                "새 사용자",
                null,
                null
        );

        given(userRepository.existsByUsername(anyString()))
                .willReturn(false);
        given(passwordEncoder.encode(anyString()))
                .willReturn("encodedPassword");
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(authenticationManager.authenticate(any()))
                .willReturn(mockAuthentication);
        given(tokenProvider.createToken(any()))
                .willReturn("token");

        // when
        authService.register(request);

        // then
        verify(userRepository, times(1)).save(argThat(user ->
                user.getRole() == UserRole.USER &&
                user.getIsActive() == true
        ));
    }
}
