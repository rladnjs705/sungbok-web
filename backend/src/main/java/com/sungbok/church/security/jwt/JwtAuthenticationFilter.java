package com.sungbok.church.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * JWT 인증 필터
 * 
 * ⚠️ 중요: @Component 제거 - 명시적 Bean 등록만 사용
 * Spring Boot 자동 등록 방지 (302 redirect 문제 해결)
 * 
 * 233번 블로그 패턴: 예외를 catch하여 permitAll()이 항상 작동하도록 보장
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    
    private static final String COOKIE_NAME = "ACCESS_TOKEN";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. Cookie에서 Access Token 추출 (우선순위 1)
            String token = resolveTokenFromCookie(request);
            
            // 2. Cookie가 없으면 Header에서 추출 (우선순위 2 - API 테스트용)
            if (!StringUtils.hasText(token)) {
                token = resolveTokenFromHeader(request);
            }

            // 3. 토큰 검증 및 인증 설정
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT 인증 성공: {}", authentication.getName());
            } else {
                log.debug("JWT 토큰이 없거나 유효하지 않음 - URI: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            // ⚠️ 233번 블로그: 예외를 catch하고 로깅만 함
            // permitAll()이 작동하도록 filterChain.doFilter() 보장
            log.debug("JWT 인증 중 예외 발생 (무시됨): {}", e.getMessage());
        }

        // ⚠️ 반드시 호출 - permitAll() 작동을 위해 필수!
        filterChain.doFilter(request, response);
    }

    /**
     * Cookie에서 Access Token 추출
     */
    private String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * Authorization Header에서 Access Token 추출
     * (API 테스트용, Cookie 방식 권장)
     */
    private String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
