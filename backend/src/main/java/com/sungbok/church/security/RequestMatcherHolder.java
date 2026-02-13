package com.sungbok.church.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 인증 제외 경로 중앙 관리 클래스
 * 
 * 234번 블로그 패턴: SecurityConfig와 JwtAuthenticationFilter의 경로 동기화
 * permitAll 경로를 한 곳에서 관리하여 불일치 방지
 */
@Component
public class RequestMatcherHolder {

    // 모든 HTTP 메서드 허용 경로
    private static final List<String> ALL_METHODS_WHITE_LIST = List.of(
        "/api/auth/**",
        "/oauth2/**",
        "/login/oauth2/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/actuator/health",
        "/api/uploads/**"  // TODO: 테스트 후 제거, 인증 필요
    );

    // GET 메서드만 허용 경로
    private static final List<String> GET_ONLY_WHITE_LIST = List.of(
        "/api/notices/**",
        "/api/sermons/**",
        "/api/worships/**",
        "/api/events/**",
        "/api/galleries/**",
        "/api/ministries/**",
        "/api/youtube/**",
        "/api/prayer-requests"
    );

    // POST 메서드만 허용 경로
    private static final List<String> POST_ONLY_WHITE_LIST = List.of(
        "/api/prayer-requests",
        "/api/notices/**"  // 테스트용
    );

    /**
     * SecurityConfig에서 사용: 모든 메서드 허용 경로 패턴
     */
    public String[] getAllMethodsWhiteList() {
        return ALL_METHODS_WHITE_LIST.toArray(new String[0]);
    }

    /**
     * SecurityConfig에서 사용: GET 전용 허용 경로 패턴
     */
    public String[] getGetOnlyWhiteList() {
        return GET_ONLY_WHITE_LIST.toArray(new String[0]);
    }

    /**
     * SecurityConfig에서 사용: POST 전용 허용 경로 패턴
     */
    public String[] getPostOnlyWhiteList() {
        return POST_ONLY_WHITE_LIST.toArray(new String[0]);
    }

    /**
     * Filter에서 사용: HttpServletRequest 직접 체크
     */
    public boolean isPermitAllRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // CORS preflight 요청은 무조건 허용
        if ("OPTIONS".equals(method)) {
            return true;
        }

        // 모든 메서드 허용 경로 체크
        for (String pattern : ALL_METHODS_WHITE_LIST) {
            if (matchesPattern(path, pattern)) {
                return true;
            }
        }

        // GET 전용 경로 체크
        if (HttpMethod.GET.matches(method)) {
            for (String pattern : GET_ONLY_WHITE_LIST) {
                if (matchesPattern(path, pattern)) {
                    return true;
                }
            }
        }

        // POST 전용 경로 체크
        if (HttpMethod.POST.matches(method)) {
            for (String pattern : POST_ONLY_WHITE_LIST) {
                if (matchesPattern(path, pattern)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesPattern(String path, String pattern) {
        String basePattern = pattern.replace("/**", "");
        if (pattern.endsWith("/**")) {
            return path.startsWith(basePattern);
        } else {
            return path.equals(basePattern);
        }
    }
}
