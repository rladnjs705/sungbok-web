package com.sungbok.church.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 * 
 * 토큰 정책:
 * - Access Token: 15분 (서버 메모리)
 * - Refresh Token: 2주 (Valkey 저장)
 * 
 * 2026년 Best Practice:
 * - 짧은 수명의 Access Token
 * - Refresh Token Rotation
 * - HS256 알고리즘
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_TYPE_KEY = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    
    // 토큰 수명
    private static final long ACCESS_TOKEN_EXPIRE_MILLIS = 1000L * 60 * 15;  // 15분
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    private SecretKey key;
    
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Access Token 생성
     * 
     * @param authentication 인증 정보
     * @return JWT Access Token
     */
    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        long now = System.currentTimeMillis();
        Date validity = new Date(now + ACCESS_TOKEN_EXPIRE_MILLIS);
        
        return Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
    
    /**
     * Access Token 생성 (Email 기반)
     * 
     * @param email 사용자 이메일
     * @param role 사용자 권한
     * @return JWT Access Token
     */
    public String generateAccessToken(String email, String role) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + ACCESS_TOKEN_EXPIRE_MILLIS);
        
        return Jwts.builder()
                .subject(email)
                .claim(AUTHORITIES_KEY, role)
                .claim(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
    
    /**
     * Token에서 Authentication 추출
     * 
     * @param token JWT Token
     * @return Authentication
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .filter(auth -> !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        
        User principal = new User(claims.getSubject(), "", authorities);
        
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    
    /**
     * Token 유효성 검증
     * 
     * @param token JWT Token
     * @return true (유효), false (유효하지 않음)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Token에서 Claims 추출
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Token 남은 유효시간 (ms)
     * 
     * @param token JWT Token
     * @return 남은 시간 (ms)
     */
    public long getExpirationMillis(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
    
    /**
     * Token에서 Subject (email) 추출
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }
    
    /**
     * Access Token 수명 반환
     */
    public long getAccessTokenExpiryMillis() {
        return ACCESS_TOKEN_EXPIRE_MILLIS;
    }
}
