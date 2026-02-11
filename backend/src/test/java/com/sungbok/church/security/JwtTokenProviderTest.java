package com.sungbok.church.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtTokenProvider 단위 테스트
 * JWT 토큰 생성 및 검증 로직 검증
 */
@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "test-secret-key-for-jwt-token-generation-and-validation-1234567890";
    private final long validityInMilliseconds = 3600000; // 1시간
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(testSecret, validityInMilliseconds);
        secretKey = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("토큰 생성 - 성공")
    void createToken_Success() {
        // Given
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", null, authorities
        );

        // When
        String token = jwtTokenProvider.createToken(authentication);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // JWT 형식 확인 (header.payload.signature)
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("토큰에 username 포함 확인")
    void createToken_ContainsUsername() {
        // Given
        String username = "testuser";
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, authorities
        );

        // When
        String token = jwtTokenProvider.createToken(authentication);

        // Then - 토큰을 파싱하여 username 확인
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(username);
    }

    @Test
    @DisplayName("토큰에 권한 정보 포함 확인")
    void createToken_ContainsAuthorities() {
        // Given
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", null, authorities
        );

        // When
        String token = jwtTokenProvider.createToken(authentication);

        // Then - 토큰을 파싱하여 권한 정보 확인
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String authoritiesString = claims.get("auth", String.class);
        assertThat(authoritiesString).contains("ROLE_USER");
        assertThat(authoritiesString).contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("유효한 토큰 검증 - 성공")
    void validateToken_ValidToken_Success() {
        // Given
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", null, authorities
        );
        String token = jwtTokenProvider.createToken(authentication);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 서명 토큰 검증 - 실패")
    void validateToken_InvalidSignature_Fail() {
        // Given - 다른 secret으로 생성된 토큰
        String differentSecret = "different-secret-key-for-jwt-token-generation-and-validation-9876543210";
        JwtTokenProvider differentProvider = new JwtTokenProvider(differentSecret, validityInMilliseconds);

        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", null, authorities
        );
        String tokenWithDifferentSignature = differentProvider.createToken(authentication);

        // When
        boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentSignature);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("형식 오류 토큰 검증 - 실패")
    void validateToken_MalformedToken_Fail() {
        // Given
        String malformedToken = "invalid.token.format";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰에서 username 추출 - 성공")
    void getUsername_ValidToken_Success() {
        // Given
        String expectedUsername = "testuser";
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                expectedUsername, null, authorities
        );
        String token = jwtTokenProvider.createToken(authentication);

        // When
        String actualUsername = jwtTokenProvider.getUsername(token);

        // Then
        assertThat(actualUsername).isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("잘못된 토큰 파싱 시 예외 발생")
    void getUsername_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid.token.format";

        // When & Then
        assertThatThrownBy(() -> jwtTokenProvider.getUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }
}
