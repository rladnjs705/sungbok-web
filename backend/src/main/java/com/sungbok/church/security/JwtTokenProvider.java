package com.sungbok.church.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

/**
 * JWT Token 생성 및 검증
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long validityInMilliseconds) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret must be configured. Please set jwt.secret property.");
        }
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 characters). Current length: " + secret.length());
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /**
     * JWT 토큰 생성
     */
    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // UTC 기준 시간 사용 (timezone 문제 방지)
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        long issueEpoch = now.atZone(ZoneId.of("UTC")).toEpochSecond();

        // plusMillis가 없어서 나노초로 변환
        LocalDateTime expiryTime = now.plusNanos(validityInMilliseconds * 1_000_000);
        long expiryEpoch = expiryTime.atZone(ZoneId.of("UTC")).toEpochSecond();

        return Jwts.builder()
                .subject(username)
                .claim("auth", authorities)
                .claim("iat", issueEpoch)    // epoch seconds 직접 설정
                .claim("exp", expiryEpoch)   // epoch seconds 직접 설정
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자명 추출
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * JWT 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
