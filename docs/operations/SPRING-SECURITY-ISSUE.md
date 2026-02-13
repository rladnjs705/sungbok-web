# Spring Security 6.x 설정 문제 보고서

## 문제 요약

`/api/uploads/**` 경로를 `permitAll()`로 설정했음에도 불구하고, OAuth2 설정으로 인해 계속 `/login`으로 302 리다이렉트됨.

## 시도한 해결책 (모두 실패)

### 1. 기본 permitAll 설정
```java
.requestMatchers("/api/uploads/**").permitAll()
```
❌ 여전히 302 리다이렉트

### 2. CSRF ignoring 추가
```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/uploads/**")
)
```
❌ 여전히 302 리다이렉트

### 3. DispatcherType permitAll
```java
.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
```
❌ 여전히 302 리다이렉트

### 4. JWT 필터 shouldNotFilter 오버라이드
```java
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/api/uploads/");
}
```
❌ 여전히 302 리다이렉트

### 5. 규칙 순서 조정
```java
// 파일 업로드를 가장 먼저 체크하도록 순서 변경
.requestMatchers("/api/uploads/**").permitAll()  // 가장 먼저
.requestMatchers("/api/auth/**").permitAll()
...
```
❌ 여전히 302 리다이젝트

### 6. exceptionHandling으로 401 반환 시도
```java
.exceptionHandling(exception -> 
    exception.authenticationEntryPoint((request, response, authException) -> {
        if (request.getRequestURI().startsWith("/api/")) {
            response.setStatus(401);
        } else {
            response.sendRedirect("/login");
        }
    })
)
```
❌ permitAll보다 exceptionHandling이 우선하여 적용됨

## 현재 설정 상태

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/notices/*/attachments", 
                    "/api/notices/with-files", "/api/notices",
                    "/api/uploads/**")
        )
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            // DispatcherType (Spring MVC 렌더링용)
            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
            
            // 파일 업로드 API (가장 먼저 체크)
            .requestMatchers("/api/uploads/**").permitAll()
            
            // 인증/인가 엔드포인트
            .requestMatchers("/api/auth/**").permitAll()
            
            // OAuth2 엔드포인트
            .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
            
            // Swagger/API Docs
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            
            // Health check
            .requestMatchers("/actuator/health").permitAll()
            
            // GET 요청 (조회)
            .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
            
            // 기도요청
            .requestMatchers("/api/prayer-requests").permitAll()
            
            // 공지사항 API
            .requestMatchers(HttpMethod.POST, "/api/notices/**").permitAll()
            
            // 관리자 전용
            .requestMatchers("/api/*/pending/**").hasRole("ADMIN")
            .requestMatchers("/api/*/approve/**").hasRole("ADMIN")
            .requestMatchers("/api/*/reject/**").hasRole("ADMIN")
            
            // 나머지 인증 필요
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> 
                userInfo.userService(customOAuth2UserService)
            )
            .successHandler(oAuth2SuccessHandler)
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

## 의심되는 원인

### 1. OAuth2Login 설정의 영향
```java
.oauth2Login(oauth2 -> ...)
```
OAuth2 로그인 설정이 `permitAll()`보다 우선적으로 동작하여, 인증되지 않은 요청을 `/login`으로 리다이렉트함.

### 2. ExceptionTranslationFilter
`AuthorizationFilter` 이후에 `ExceptionTranslationFilter`가 실행되어, `AccessDeniedException`을 catch하고 `/login`으로 리다이렉트함.

### 3. FilterChainProxy 순서
Spring Security 6.x에서 FilterChainProxy의 필터 순서가 변경되어, 기존 방식과 다르게 동작할 가능성.

## 해결 방안 후보

### 방안 1: SecurityFilterChain 분리
```java
@Bean
@Order(1)  // 우선순위 높게
public SecurityFilterChain uploadFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/api/uploads/**")  // 특정 경로만 처리
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        )
        .csrf(csrf -> csrf.disable());
    return http.build();
}

@Bean
@Order(2)
public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
    // 기존 설정
}
```

### 방안 2: ExceptionTranslationFilter 커스터마이징
```java
http
    .exceptionHandling(exception -> exception
        .defaultAuthenticationEntryPointFor(
            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
            request -> request.getRequestURI().startsWith("/api/uploads/")
        )
    )
```

### 방안 3: FilterRegistrationBean으로 필터 순서 조정
```java
@Bean
public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
        JwtAuthenticationFilter filter) {
    FilterRegistrationBean<JwtAuthenticationFilter> registration = 
        new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);  // Spring Security chain에서 제외
    return registration;
}
```

### 방안 4: Spring Security 6.x 마이그레이션 가이드 참고
- https://docs.spring.io/spring-security/reference/migration/index.html
- `authorizeRequests` → `authorizeHttpRequests` 마이그레이션 확인
- `FilterSecurityInterceptor` → `AuthorizationFilter` 변경사항 확인

## 테스트 방법

```bash
# 1. 정상 요청 테스트
curl -v -X POST "http://localhost:8081/api/uploads/presign" \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.txt","folder":"notices","contentType":"text/plain","fileSize":100,"checksum":"abc123"}'

# 2. 응답 확인
# Expected: HTTP/1.1 200 OK
# Actual:   HTTP/1.1 302 Location: http://localhost:8081/login
```

## 관련 문서

- Spring Security 6.x Reference: https://docs.spring.io/spring-security/reference/
- AuthorizationFilter: https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
- OAuth2 Login: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html

## 비고

- 현재 프로젝트: Spring Boot 4.0.2, Spring Security 6.x
- OAuth2 설정이 있는 상태에서 `permitAll()`이 제대로 동작하지 않는 문제
- JWT 필터는 `shouldNotFilter`로 업로드 경로를 건너뛰도록 설정되어 있음

---

**해결 우선순위**: 높음 (파일 업로드 기능 구현 완료, 배포 전 해결 필요)
