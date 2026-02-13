# Spring Security 7.x 치트시트

> 빠른 참고용 요약 문서

---

## 🔴 302 Redirect 긴급 체크리스트

```
□ JwtAuthenticationFilter에 @Component 없음
□ doFilterInternal()에서 try-catch로 예외 처리
□ filterChain.doFilter() 반드시 호출
□ formLogin(form -> form.disable()) 설정
□ SecurityConfig에서 단일 FilterChain 사용
```

---

## 필수 코드 템플릿

### 1. SecurityConfig (단일 체인)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final RequestMatcherHolder requestMatcherHolder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/uploads/**"))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())  // 302 방지
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(requestMatcherHolder.getAllMethodsWhiteList()).permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(
                jwtAuthenticationFilter(),  // @Bean 메서드
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean  // @Component 아님!
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
}
```

### 2. JwtAuthenticationFilter

```java
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.debug("JWT 오류: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);  // 반드시 호출!
    }
    
    private String resolveToken(HttpServletRequest request) {
        // Cookie 우선
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // Header 차선
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        
        return null;
    }
}
```

### 3. RequestMatcherHolder

```java
@Component
public class RequestMatcherHolder {
    
    private static final List<String> PERMIT_ALL = List.of(
        "/api/auth/**",
        "/api/uploads/**",
        "/oauth2/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    );
    
    public String[] getAllMethodsWhiteList() {
        return PERMIT_ALL.toArray(new String[0]);
    }
    
    public boolean isPermitAllRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // OPTIONS 허용
        if ("OPTIONS".equals(method)) return true;
        
        // 경로 매칭
        for (String pattern : PERMIT_ALL) {
            if (path.startsWith(pattern.replace("/**", ""))) {
                return true;
            }
        }
        return false;
    }
}
```

### 4. Controller (익명 사용자 처리)

```java
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class FileUploadController {
    
    @PostMapping("/presign")
    public ResponseEntity<PresignUrlResponse> presign(
            @Valid @RequestBody PresignUrlRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userId = (userDetails != null) 
            ? userDetails.getUsername() 
            : "anonymous";
        
        // ...
    }
}
```

---

## 디버깅 명령어

```bash
# 302 Redirect 확인
curl -v -X POST http://localhost:8081/api/uploads/presign \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}' 2>&1 | grep -E "(HTTP|Location)"

# CORS Preflight 확인
curl -v -X OPTIONS http://localhost:8081/api/uploads/presign \
  -H "Origin: http://localhost:3001" \
  -H "Access-Control-Request-Method: POST" 2>&1 | grep -E "HTTP"

# JWT 쿠키 테스트
curl http://localhost:8081/api/protected \
  -H "Cookie: ACCESS_TOKEN=eyJhbG..."
```

---

## 흔한 실수 TOP 5

| # | 실수 | 증상 | 해결 |
|---|------|------|------|
| 1 | `@Component` 사용 | 302 redirect | `@Bean`으로 변경 |
| 2 | 예외 미처리 | permitAll() 무시 | try-catch 추가 |
| 3 | `filterChain.doFilter()` 누락 | 후속 필터 실행 안 됨 | 메서드 마지막에 호출 |
| 4 | 다중 `@Order` 체인 | 우선순위 혼란 | 단일 체인 사용 |
| 5 | `@AuthenticationPrincipal` null 체크 누락 | NPE | null 체크 필수 |

---

## 관련 파일 경로

```
backend/
├── src/main/java/com/sungbok/church/
│   ├── config/SecurityConfig.java
│   ├── security/
│   │   ├── RequestMatcherHolder.java
│   │   └── jwt/JwtAuthenticationFilter.java
│   └── controller/FileUploadController.java
└── docs/
    ├── SECURITY_ARCHITECTURE.md      # 상세 문서
    └── SECURITY_CHEATSHEET.md        # 본 파일
```
