# Spring Security 7.x 문제 연구 보고서

## 시도한 해결책 (모두 실패)

### 1. SecurityFilterChain 분리 (@Order 사용)
```java
@Bean
@Order(1)
public SecurityFilterChain uploadSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/api/uploads/**")
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .exceptionHandling(exception -> {
            exception.authenticationEntryPoint((req, res, ex) -> res.setStatus(401));
            exception.accessDeniedHandler((req, res, ex) -> res.setStatus(403));
        });
    return http.build();
}

@Bean
@Order(2)
public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/uploads/**").permitAll()  // 중복 등록
            ...
        )
        .oauth2Login(...)
        .exceptionHandling(...);
    return http.build();
}
```
**결과**: 여전히 302 리다이렉트

### 2. exceptionHandling 커스터마이징
두 체인 모두에 추가:
```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint((request, response, authException) -> {
        response.setStatus(401);
    })
    .accessDeniedHandler((request, response, accessDeniedException) -> {
        response.setStatus(403);
    })
)
```
**결과**: 여전히 302 리다이렉트

### 3. CSRF 완전 비활성화
```java
.csrf(csrf -> csrf.disable())
```
**결과**: 여전히 302 리다이렉트

### 4. permitAll() 중복 등록
두 체인 모두에 `/api/uploads/**`를 `permitAll()`로 등록
**결과**: 여전히 302 리다이렉트

## 의심되는 원인

### 1. OAuth2Login 글로벌 설정
`oauth2Login()`이 모든 필터 체인에 영향을 미칠 수 있음. Spring Security 7.x에서 OAuth2 설정이 바뀌었을 가능성.

### 2. ExceptionTranslationFilter 순서
`ExceptionTranslationFilter`가 `AuthorizationFilter`보다 먼저 실행되어 예외를 catch하고 리다이렉트를 발생시킴.

### 3. FilterChainProxy 매칭 문제
`@Order`와 `securityMatcher`의 조합이 예상대로 동작하지 않을 수 있음.

### 4. Spring Security 7.x 버그
`permitAll()`과 `oauth2Login`의 조합에서 버그가 있을 가능성.

## 다음 단계

### Spring Security 7.x 공식 문서 확인 필요

1. **Migration Guide**: https://docs.spring.io/spring-security/reference/migration-7/index.html
2. **Servlet Security**: https://docs.spring.io/spring-security/reference/servlet/configuration/java.html
3. **OAuth2 Login**: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html
4. **Multiple Chains**: https://docs.spring.io/spring-security/reference/servlet/configuration/java.html#_multiple_httpsecurity_instances

### 확인할 사항

1. `securityMatcher`와 `@Order`의 정확한 동작 방식
2. `oauth2Login`이 필터 체인에 미치는 영향
3. `ExceptionTranslationFilter`의 동작 방식 변경사항
4. `permitAll()`과 OAuth2 설정의 상호작용

### 대안 해결책

#### 대안 1: Controller에서 permitAll 사용
```java
@RestController
@RequestMapping("/api/uploads")
@PreAuthorize("permitAll()")  // 메서드 레벨에서 설정
public class FileUploadController {
    ...
}
```

#### 대안 2: WebSecurityCustomizer 사용
```java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/api/uploads/**");
}
```
⚠️ 보안 헤더가 적용되지 않음

#### 대안 3: FilterRegistrationBean으로 필터 순서 조정
```java
@Bean
public FilterRegistrationBean<Filter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
}
```

#### 대안 4: Spring Authorization Server 사용
Spring Security 7.x에 통합된 Authorization Server 사용

## 현재 코드 상태

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class OAuth2Config {

    @Bean
    @Order(1)
    public SecurityFilterChain uploadSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/uploads/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((req, res, ex) -> res.setStatus(401))
                .accessDeniedHandler((req, res, ex) -> res.setStatus(403))
            );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/notices/*/attachments", ...)
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/uploads/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                ...
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Unauthorized\"}");
                })
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

## 테스트 방법

```bash
curl -v -X POST "http://localhost:8081/api/uploads/presign" \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.txt","folder":"notices","contentType":"text/plain","fileSize":100,"checksum":"abc123"}'

# Expected: HTTP/1.1 200 OK
# Actual:   HTTP/1.1 302 Location: http://localhost:8081/login
```

---

**상태**: 해결 필요  
**우선순위**: 높음  
**차단**: Pre-signed URL 파일 업로드 기능 테스트
