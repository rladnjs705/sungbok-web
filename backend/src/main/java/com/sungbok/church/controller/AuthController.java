package com.sungbok.church.controller;

import com.sungbok.church.api.dto.request.LoginRequest;
import com.sungbok.church.api.dto.request.RegisterRequest;
import com.sungbok.church.domain.entity.User;
import com.sungbok.church.domain.enums.UserRole;
import com.sungbok.church.domain.repository.UserRepository;
import com.sungbok.church.security.jwt.JwtTokenProvider;
import com.sungbok.church.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;

/**
 * 인증/인가 관련 API
 * 
 * 기능:
 * - 일반 로그인 (폼 로그인)
 * - 회원가입
 * - OAuth2 로그인 (구글, 네이버, 카카오)
 * - 로그아웃
 * - 토큰 갱신
 * 
 * 2026년 Best Practice:
 * - 일반/OAuth 통일된 Cookie 기반 인증
 * - HttpOnly + Secure + SameSite=Strict
 * - Refresh Token 2주 (Valkey)
 * - Access Token 15분
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증/인가 관련 API (일반 + OAuth)")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String COOKIE_NAME = "ACCESS_TOKEN";
    private static final Duration COOKIE_MAX_AGE = Duration.ofMinutes(15);

    // ==================== 일반 로그인 ====================

    /**
     * 일반 로그인 (폼 로그인)
     * 
     * 로그인 성공 시:
     * 1. Access Token → HttpOnly Cookie (15분)
     * 2. Refresh Token → Valkey (2주)
     */
    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "아이디/비밀번호로 로그인 (Cookie 기반)")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, 
                                   HttpServletResponse response) {
        
        try {
            // 인증 수행
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 사용자 정보 조회
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다"));

            // OAuth 사용자는 일반 로그인 불가
            if (user.isOAuthUser()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "소셜 로그인으로 가입된 계정입니다"));
            }

            // Access Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            
            // Refresh Token 생성 및 Valkey 저장
            tokenService.createRefreshToken(user.getEmail());

            // Cookie 설정
            setAccessTokenCookie(response, accessToken);

            log.info("일반 로그인 성공: {}", request.getUsername());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 되었습니다",
                    "email", user.getEmail(),
                    "name", user.getName()
            ));

        } catch (Exception e) {
            log.warn("로그인 실패: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "아이디 또는 비밀번호가 올바르지 않습니다"));
        }
    }

    /**
     * 회원가입
     * 
     * 회원가입 후 자동 로그인 (Cookie 설정)
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "일반 회원가입 후 자동 로그인")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      HttpServletResponse response) {
        
        // 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "이미 사용 중인 아이디입니다"));
        }

        // 이메일 중복 체크 (일반 + OAuth)
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            // OAuth로 가입된 이메일인지 확인
            User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (existingUser != null && existingUser.isOAuthUser()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false, 
                                "message", "이미 소셜 로그인으로 가입된 이메일입니다. " + 
                                          existingUser.getProvider().getDisplayName() + "로 로그인해주세요."
                        ));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "이미 사용 중인 이메일입니다"));
        }

        // 사용자 생성
        User user = User.createLocalUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );

        userRepository.save(user);

        // 자동 로그인
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        tokenService.createRefreshToken(user.getEmail());
        setAccessTokenCookie(response, accessToken);

        log.info("회원가입 및 자동 로그인: {}", request.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "success", true,
                        "message", "회원가입이 완료되었습니다",
                        "email", user.getEmail(),
                        "name", user.getName()
                ));
    }

    // ==================== 공통 기능 ====================

    /**
     * 로그아웃 (일반/OAuth 공통)
     * 
     * 1. Access Token 블랙리스트 등록
     * 2. Refresh Token 삭제
     * 3. Cookie 삭제
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "일반/OAuth 공통 로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        
        String accessToken = resolveTokenFromCookie(request);
        
        if (accessToken != null) {
            // Access Token 블랙리스트 등록
            long expirationMillis = jwtTokenProvider.getExpirationMillis(accessToken);
            if (expirationMillis > 0) {
                tokenService.blacklistAccessToken(accessToken, expirationMillis);
            }
            
            // Refresh Token 삭제
            try {
                String email = jwtTokenProvider.getSubject(accessToken);
                tokenService.revokeAllTokens(email);
                log.info("로그아웃: {}", email);
            } catch (Exception e) {
                log.warn("Refresh Token 삭제 실패");
            }
        }
        
        // Cookie 삭제
        deleteCookie(response);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "로그아웃 되었습니다"
        ));
    }

    /**
     * 토큰 갱신 (Refresh Token Rotation)
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token Rotation")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        
        String oldAccessToken = resolveTokenFromCookie(request);
        
        if (oldAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Access Token이 없습니다"));
        }
        
        String email = jwtTokenProvider.getSubject(oldAccessToken);
        
        // Refresh Token Rotation
        String[] result = tokenService.rotateRefreshToken(email);
        
        if (result == null) {
            deleteCookie(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "세션이 만료되었습니다. 다시 로그인해주세요."));
        }
        
        String userEmail = result[1];
        
        // 새 Access Token 생성
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다"));
        
        String newAccessToken = jwtTokenProvider.generateAccessToken(userEmail, user.getRole().toAuthority());
        
        // Cookie에 새 Access Token 설정
        setAccessTokenCookie(response, newAccessToken);
        
        log.info("토큰 갱신: {}", userEmail);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "토큰이 갱신되었습니다",
                "email", userEmail
        ));
    }

    /**
     * 현재 로그인한 사용자 정보
     */
    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보", description = "로그인 상태 확인 및 사용자 정보 조회")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다"));
        }
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole().name(),
                "provider", user.getProvider() != null ? user.getProvider().name() : "LOCAL"
        ));
    }

    // ==================== Private Methods ====================

    private String resolveTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        
        for (var cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .build();
        
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void deleteCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
