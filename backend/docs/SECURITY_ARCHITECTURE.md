# Spring Security 7.x 아키텍처 및 JWT 인증 프로세스

> **버전**: Spring Boot 4.0.2, Spring Security 7.x  
> **작성일**: 2026-02-13  
> **작성자**: Kimi Code CLI  
> **목적**: 302 redirect 및 인증 문제 방지를 위한 가이드

---

## 📋 목차

1. [개요](#개요)
2. [아키텍처 개요](#아키텍처-개요)
3. [JWT 인증 프로세스](#jwt-인증-프로세스)
4. [Security Filter Chain](#security-filter-chain)
5. [주요 문제 및 해결책](#주요-문제-및-해결책)
6. [코딩 가이드라인](#코딩-가이드라인)
7. [디버깅 가이드](#디버깅-가이드)

---

## 개요

### 302 Redirect 문제의 원인

Spring Security에서 `permitAll()`을 설정했음에도 불구하고 302 redirect가 발생하는 주요 원인:

1. **JWT Filter의 @Component 자동 등록**: Spring Boot가 필터를 모든 FilterChain에 자동 추가
2. **예외 처리 미흡**: Filter에서 예외를 던지면 `filterChain.doFilter()`가 호출되지 않음
3. **경로 불일치**: `permitAll` 경로와 `shouldNotFilter` 경로가 불일치

### 해결 패턴 (Blog 233, 234, 235)

| 블로그 | 핵심 내용 | 적용 위치 |
|--------|----------|----------|
| 233 | `doFilterInternal()`에서 예외를 catch하고 반드시 `filterChain.doFilter()` 호출 | `JwtAuthenticationFilter` |
| 234 | `RequestMatcherHolder`로 경로 중앙 관리 | `RequestMatcherHolder` |
| 235 | 다중 `@Order` 체인 대신 단일 FilterChain 사용 | `SecurityConfig` |

---

## 아키텍처 개요

### 전체 인증 흐름

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Request                          │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  1. CORS Filter (CorsConfigurationSource)                       │
│     - credentials: true                                         │
│     - allowedOrigins: localhost:3001, production domains       │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  2. CsrfFilter                                                  │
│     - /api/uploads/**: disabled                                 │
│     - /api/notices/**: disabled for specific paths             │
│     - 그 외: CSRF 토큰 검증                                     │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  3. JwtAuthenticationFilter                                     │
│     - Cookie: ACCESS_TOKEN 확인                                 │
│     - Header: Authorization Bearer 확인                         │
│     - 토큰 유효 시 SecurityContext에 Authentication 설정        │
│     - ⚠️ 예외 발생필도 filterChain.doFilter() 보장             │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  4. AuthorizationFilter (permitAll/authenticated/hasRole)       │
│     - RequestMatcherHolder로 경로 매칭                         │
│     - permitAll: 인증 없이 통과                                 │
│     - authenticated: 인증 필수                                  │
│     - hasRole("ADMIN"): 권한 확인                              │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Controller                              │
└─────────────────────────────────────────────────────────────────┘
```

### 패키지 구조

```
com.sungbok.church/
├── config/
│   └── SecurityConfig.java          # 메인 시큐리티 설정 (단일 FilterChain)
├── security/
│   ├── RequestMatcherHolder.java    # 경로 중앙 관리
│   ├── jwt/
│   │   ├── JwtAuthenticationFilter.java    # JWT 필터 (@Component ❌)
│   │   └── JwtTokenProvider.java           # 토큰 생성/검증
│   └── oauth/
│       ├── CustomOAuth2UserService.java    # OAuth2 사용자 서비스
│       └── OAuth2SuccessHandler.java       # OAuth2 성공 핸들러
└── controller/
    └── FileUploadController.java    # @AuthenticationPrincipal 사용 예시
```

---

## JWT 인증 프로세스

### 1. 토큰 추출 우선순위

```java
// JwtAuthenticationFilter.doFilterInternal()

// 1순위: HttpOnly Cookie
String token = resolveTokenFromCookie(request);

// 2순위: Authorization Header (API 테스트용)
if (!StringUtils.hasText(token)) {
    token = resolveTokenFromHeader(request);
}
```

### 2. 토큰 검증 및 인증 설정

```java
if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
    // 토큰에서 인증 정보 추출
    Authentication authentication = jwtTokenProvider.getAuthentication(token);
    
    // SecurityContext에 저장 (ThreadLocal)
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

### 3. Controller에서 사용

```java
@PostMapping("/presign")
public ResponseEntity<PresignUrlResponse> generatePresignedUrl(
        @Valid @RequestBody PresignUrlRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {  // null 가능
    
    // 익명 사용자 처리 필수
    String username = (userDetails != null) ? userDetails.getUsername() : "anonymous";
    // ...
}
```

### 4. Token Payload 구조

```json
{
  "sub": "user@example.com",
  "roles": ["USER", "ADMIN"],
  "iat": 1707782400,
  "exp": 1707786000
}
```

---

## Security Filter Chain

### 단일 FilterChain 패턴 (권장)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF 설정 (특정 경로 제외)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/uploads/**",
                    "/api/notices/*/attachments"
                )
            )
            
            // 세션 Stateless
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // formLogin 완전 비활성화 (302 방지)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // 인가 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(requestMatcherHolder.getAllMethodsWhiteList()).permitAll()
                .requestMatchers(HttpMethod.GET, requestMatcherHolder.getGetOnlyWhiteList()).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            // OAuth2 (선택)
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
            )
            
            // JWT 필터 추가 (@Component 아님!)
            .addFilterBefore(
                jwtAuthenticationFilter(),  // Bean 메서드 호출
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    // ⚠️ 중요: @Component가 아닌 @Bean으로 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
}
```

### 다중 FilterChain 패턴 (비권장 - 문제 발생 가능)

```java
// ❌ 비권장: @Order 사용 시 발생 가능한 문제
@Configuration
public class SecurityConfig {
    
    @Bean
    @Order(0)  // 우선순위
    public SecurityFilterChain uploadChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/uploads/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
    
    @Bean
    @Order(1)
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, ...);  // @Component 필터
        return http.build();
    }
}
// 문제: @Component 필터가 @Order(0) 체인에도 자동 등록될 수 있음
```

---

## 주요 문제 및 해결책

### 문제 1: 302 Redirect to /login

**증상**:
```
POST /api/uploads/presign → 302 Found → Location: /login
```

**원인**:
1. `JwtAuthenticationFilter`가 `@Component`로 등록 → 모든 체인에 자동 추가
2. 필터에서 예외 발생 → `filterChain.doFilter()` 호출 실패
3. Spring Security가 인증 실패로 판단 → `/login`으로 redirect

**해결**:
```java
// 1. @Component 제거
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // ...
    
    @Override
    protected void doFilterInternal(...) {
        try {
            // 인증 로직
        } catch (Exception e) {
            // 예외를 catch하여 permitAll() 보장
            log.debug("JWT 인증 중 예외: {}", e.getMessage());
        }
        
        // ⚠️ 반드시 호출
        filterChain.doFilter(request, response);
    }
}

// 2. @Bean으로 명시적 등록
@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtTokenProvider);
}
```

### 문제 2: CORS Preflight (OPTIONS) 401

**증상**:
```
OPTIONS /api/uploads/presign → 401 Unauthorized
```

**원인**: OPTIONS 요청이 JWT 필터를 통과하면서 인증 실패

**해결**:
```java
@Component
public class RequestMatcherHolder {
    
    public boolean isPermitAllRequest(HttpServletRequest request) {
        String method = request.getMethod();
        
        // CORS preflight 허용
        if ("OPTIONS".equals(method)) {
            return true;
        }
        // ...
    }
}
```

### 문제 3: @AuthenticationPrincipal null

**증상**: `permitAll()` 경로에서 `@AuthenticationPrincipal`이 null

**해결**:
```java
@PostMapping("/presign")
public ResponseEntity<?> upload(
        @AuthenticationPrincipal UserDetails userDetails) {
    
    // 익명 사용자 처리
    String userId = (userDetails != null) 
        ? userDetails.getUsername() 
        : "anonymous";
    // ...
}
```

### 문제 4: CSRF 403

**증상**: POST 요청 시 403 Forbidden

**해결**:
```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers(
        "/api/uploads/**",      // 파일 업로드 제외
        "/api/auth/**"          // 인증 API 제외
    )
)
```

---

## 코딩 가이드라인

### ✅ Do's

```java
// 1. 단일 FilterChain 사용
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // ...
}

// 2. @Bean으로 필터 등록 (not @Component)
@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtTokenProvider);
}

// 3. 예외 처리 필수
try {
    // 인증 로직
} catch (Exception e) {
    log.debug("예외: {}", e.getMessage());
}
filterChain.doFilter(request, response);  // 반드시 호출

// 4. 경로 중앙 관리
@Component
public class RequestMatcherHolder {
    public boolean isPermitAllRequest(HttpServletRequest request) {
        // 모든 permitAll 경로 관리
    }
}

// 5. 익명 사용자 처리
@AuthenticationPrincipal UserDetails userDetails
String userId = (userDetails != null) ? userDetails.getUsername() : "anonymous";
```

### ❌ Don'ts

```java
// 1. @Component로 필터 등록 (자동 등록됨)
@Component  // ❌ 금지
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // ...
}

// 2. 예외를 던지는 필터
try {
    // ...
    filterChain.doFilter(request, response);
} catch (JwtException e) {
    throw e;  // ❌ 금지 - permitAll() 무시됨
}

// 3. 다중 @Order 체인 + @Component 필터
@Bean
@Order(0)
public SecurityFilterChain chain1(...) {  // ❌ 복잡성 증가
    // ...
}

// 4. @AuthenticationPrincipal null 체크 없이 사용
@AuthenticationPrincipal UserDetails userDetails
String username = userDetails.getUsername();  // ❌ NPE 위험
```

---

## 디버깅 가이드

### 로그 레벨 설정

```yaml
# application.yml
logging:
  level:
    org.springframework.security: DEBUG
    com.sungbok.church.security: DEBUG
```

### 유용한 디버깅 정보

```java
// JwtAuthenticationFilter에 추가
@Override
protected void doFilterInternal(...) {
    log.debug("Request URI: {}", request.getRequestURI());
    log.debug("Request Method: {}", request.getMethod());
    log.debug("Token from Cookie: {}", resolveTokenFromCookie(request));
    log.debug("Token from Header: {}", resolveTokenFromHeader(request));
    
    // ...
}
```

### curl 테스트 명령어

```bash
# 1. Health Check
curl http://localhost:8081/actuator/health

# 2. 파일 업로드 (인증 없이 - permitAll)
curl -X POST http://localhost:8081/api/uploads/presign \
  -H "Content-Type: application/json" \
  -d '{
    "filename": "test.pdf",
    "contentType": "application/pdf",
    "size": 1048576,
    "entityType": "NOTICE",
    "entityId": 1,
    "folder": "notices",
    "fileSize": 1048576
  }'

# 3. CORS Preflight 테스트
curl -X OPTIONS http://localhost:8081/api/uploads/presign \
  -H "Origin: http://localhost:3001" \
  -H "Access-Control-Request-Method: POST" \
  -v

# 4. 인증 필요 API (JWT 쿠키)
curl http://localhost:8081/api/notices \
  -H "Cookie: ACCESS_TOKEN=eyJhbGciOiJIUzI1NiIs..."
```

---

## 참고 자료

- [Spring Security 7.x 공식 문서](https://docs.spring.io/spring-security/reference/index.html)
- [Blog 233: permitAll() 작동 원리](https://kim-jong-hyun.tistory.com/233)
- [Blog 234: RequestMatcherHolder 패턴](https://kim-jong-hyun.tistory.com/234)
- [Blog 235: securityMatcher() 사용법](https://kim-jong-hyun.tistory.com/235)

---

## 변경 이력

| 날짜 | 버전 | 설명 |
|------|------|------|
| 2026-02-13 | 1.0 | 초기 작성 - 302 redirect 문제 해결 |
