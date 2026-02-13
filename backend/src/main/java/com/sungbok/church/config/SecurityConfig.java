package com.sungbok.church.config;

import com.sungbok.church.security.RequestMatcherHolder;
import com.sungbok.church.security.jwt.JwtAuthenticationFilter;
import com.sungbok.church.security.jwt.JwtTokenProvider;
import com.sungbok.church.security.oauth.CustomOAuth2UserService;
import com.sungbok.church.security.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 7.x 통합 설정
 * 
 * 302 redirect 문제 해결을 위한 단일 FilterChain 패턴
 * - @Component 제거된 JwtAuthenticationFilter 명시적 등록
 * - RequestMatcherHolder로 경로 중앙 관리 (234번 블로그)
 * - 단일 SecurityFilterChain (235번 블로그)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final RequestMatcherHolder requestMatcherHolder;

    /**
     * 단일 SecurityFilterChain
     * 
     * 235번 블로그 패턴: 다중 체인(@Order) 대신 단일 체인 + 명시적 필터 제어
     * 233번 블로그 패턴: 예외를 catch하여 permitAll()이 항상 작동하도록 보장
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/notices/*/attachments",
                    "/api/notices/with-files",
                    "/api/notices",
                    "/api/uploads/**"  // 파일 업로드도 CSRF 제외
                )
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // ⚠️ 302 redirect 방지: formLogin 완전 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // ⚠️ 234번 블로그: 중앙화된 경로 관리 사용
            .authorizeHttpRequests(auth -> auth
                // 모든 메서드 허용 경로
                .requestMatchers(requestMatcherHolder.getAllMethodsWhiteList()).permitAll()
                // GET 전용 허용 경로
                .requestMatchers(HttpMethod.GET, requestMatcherHolder.getGetOnlyWhiteList()).permitAll()
                // POST 전용 허용 경로
                .requestMatchers(HttpMethod.POST, requestMatcherHolder.getPostOnlyWhiteList()).permitAll()
                // 관리자 전용
                .requestMatchers("/api/*/pending/**", "/api/*/approve/**", "/api/*/reject/**").hasRole("ADMIN")
                // 나머지 인증 필요
                .anyRequest().authenticated()
            )
            
            // 인증 실패 시 401 반환 (302 redirect 방지)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> 
                    userInfo.userService(customOAuth2UserService)
                )
                .successHandler(oAuth2SuccessHandler)
            )
            .authenticationProvider(authenticationProvider())
            
            // ⚠️ 235번 블로그: @Component 제거된 필터를 명시적으로 추가
            // Spring Boot 자동 등록 방지
            .addFilterBefore(
                jwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * @Component 제거된 필터를 Bean으로 명시적 등록
     * Spring Boot 자동 등록 방지 (302 redirect 문제 해결 핵심)
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
    
    /**
     * 인증 실패 시 401 반환 (302 redirect 방지)
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"인증이 필요합니다\"}");
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3001",
            "https://www.sungbok-church.com",
            "https://admin.sungbok-church.com"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
