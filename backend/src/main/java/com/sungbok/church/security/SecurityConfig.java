package com.sungbok.church.security;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * JWT 기반 인증 및 역할 기반 접근 제어
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 인증 엔드포인트 - 공개
                        .requestMatchers("/api/auth/**").permitAll()

                        // Health check - 공개
                        .requestMatchers("/actuator/health").permitAll()

                        // GET 요청 - 공개 (조회)
                        .requestMatchers(HttpMethod.GET, "/api/notices/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sermons/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/worships/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/galleries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/video-galleries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/testimonies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ministries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/missions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/donation-accounts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/youtube/lives").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/youtube/playlists").permitAll()

                        // 승인된 기도요청 조회 - 공개
                        .requestMatchers(HttpMethod.GET, "/api/prayer-requests").permitAll()

                        // 기도요청 생성 - 공개 (일반 사용자가 요청 가능)
                        .requestMatchers(HttpMethod.POST, "/api/prayer-requests").permitAll()

                        // 대기 중인 콘텐츠 조회 - 관리자 전용
                        .requestMatchers("/api/*/pending/**").hasRole("ADMIN")

                        // 승인/거부 작업 - 관리자 전용
                        .requestMatchers("/api/*/approve/**").hasRole("ADMIN")
                        .requestMatchers("/api/*/reject/**").hasRole("ADMIN")

                        // YouTube Quota 통계 - 관리자 전용
                        .requestMatchers("/api/youtube/quota/**").hasRole("ADMIN")

                        // CUD 작업 (POST, PUT, DELETE) - 관리자 전용
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        // 나머지 모든 요청 - 인증 필요
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
