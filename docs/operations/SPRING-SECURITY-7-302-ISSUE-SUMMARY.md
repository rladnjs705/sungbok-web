# Spring Security 7.x 302 리다이렉트 문제 종합 보고서

## 문제 요약

`/api/uploads/**` 경로를 `permitAll()`로 설정했음에도 불구하고, 계속 `/login`으로 302 리다이렉트됨.

## 시도한 해결책 (모두 실패)

### 1. SecurityFilterChain 분리 with @Order
- `UploadSecurityConfig` 클래스 생성 (@Order(0))
- `OAuth2Config` 클래스 수정 (@Order(1))
- `securityMatcher`로 경로 분리
- **결과**: 실패 (302 리다이렉트 지속)

### 2. formLogin, httpBasic, csrf 비활성화
```java
.csrf(AbstractHttpConfigurer::disable)
.formLogin(AbstractHttpConfigurer::disable)
.httpBasic(AbstractHttpConfigurer::disable)
```
- **결과**: 실패

### 3. exceptionHandling 커스터마이징
```java
.exceptionHandling(exception -> 
    exception.authenticationEntryPoint((req, res, ex) -> res.setStatus(401))
)
```
- **결과**: 실패

### 4. 구체적인 securityMatcher 설정
```java
.securityMatcher("/api/auth/**", "/api/notices/**", ...)  // /api/uploads/** 제외
```
- **결과**: 실패

### 5. @EnableWebSecurity 충돌 제거
- `UploadSecurityConfig`에서 `@EnableWebSecurity` 제거
- **결과**: 실패

## 현재 코드 구조

### UploadSecurityConfig.java (@Order(0))
```java
@Configuration
public class UploadSecurityConfig {
    @Bean
    @Order(0)
    public SecurityFilterChain uploadFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/uploads/**", "/uploads/**")
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```

### OAuth2Config.java (@Order(1))
```java
@Bean
@Order(1)
public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/api/auth/**", "/api/notices/**", ...)  // uploads 제외
        .cors(...)
        .csrf(...)
        .sessionManagement(...)
        .authorizeHttpRequests(auth -> auth...)
        .oauth2Login(...)
        ...
    return http.build();
}
```

## 의심되는 원인

### 1. Spring Security 7.x의 FilterChainProxy 동작 변경
- Spring Security 6.x에서 7.x로 오면서 FilterChainProxy의 동작 방식이 변경되었을 가능성
- `securityMatcher`와 `@Order`의 상호작용이 예상과 다를 수 있음

### 2. OAuth2Login의 전역 영향
- `oauth2Login()` 설정이 모든 요청에 영향을 미칠 수 있음
- `permitAll()`보다 OAuth2 설정이 우선적으로 적용될 수 있음

### 3. ExceptionTranslationFilter
- `ExceptionTranslationFilter`가 `AuthorizationFilter`보다 먼저 실행되어 예외를 catch하고 리다이렉트
- `permitAll()`은 AuthorizationFilter에서 처리되지만, 그 이전 필터에서 이미 리다이렉트 발생 가능

### 4. JwtAuthenticationFilter
- `JwtAuthenticationFilter`가 `shouldNotFilter`로 `/api/uploads/`를 걸지만, 다른 필터에서 문제 발생 가능

## 추가 조사 필요 사항

### Spring Security 7.x 공식 문서
1. Multiple SecurityFilterChain 정확한 동작 방식
2. FilterChainProxy의 필터 순서
3. `securityMatcher`와 `@Order`의 상호작용
4. OAuth2 설정이 다른 필터 체인에 미치는 영향

### 디버깅 방법
```bash
# Spring Security 디버그 로그 활성화
-Dlogging.level.org.springframework.security=DEBUG

# 필터 체인 확인
-Dlogging.level.org.springframework.security.web.FilterChainProxy=DEBUG
```

## 임시 해결책 (권장)

### 1. 별도의 서블릿 컨텍스트 사용
```java
// application.properties
server.servlet.context-path=/api

// 그리고 uploads는 별도 포트나 경로로 분리
```

### 2. Controller 레벨에서 처리
```java
@RestController
@RequestMapping("/api/uploads")
@PreAuthorize("permitAll()")  // 또는 @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
public class FileUploadController {
    ...
}
```

### 3. WebSecurityCustomizer 사용 (보안 헤더 미적용)
```java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/api/uploads/**");
}
```

### 4. Spring Authorization Server 마이그레이션
- Spring Security 7.x에 통합된 Authorization Server 사용
- 더 세밀한 인증/인가 제어 가능

## 결론

현재 Spring Security 7.x의 복잡한 설정 문제로 인해 `/api/uploads/**` 경로의 302 리다이렉트 문제를 해결하지 못했습니다.

**권장사항**:
1. Spring Security 7.x 공식 문서 및 이슈 트래커 확인
2. Spring 팀에 문의 또는 Stack Overflow에 질문
3. 임시 해결책(위 4가지 중 하나) 적용 후 추후 정식 해결책 적용

**현재 구현 상태**:
- ✅ FileUpload Entity, Repository, Controller, Service 구현 완료
- ✅ 프론트엔드 Hook, 컴포넌트, API Routes 구현 완료
- ❌ Spring Security 설정 문제로 인해 테스트 불가

---

**작성일**: 2026-02-13  
**상태**: 해결 필요  
**차단**: 파일 업로드 API 테스트
