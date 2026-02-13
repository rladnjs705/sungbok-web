package com.sungbok.church.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Token Service
 * 
 * JWT Token 관리 (Valkey/Redis 사용)
 * 
 * 저장 정책:
 * - Refresh Token: Valkey (2주 TTL)
 * - Access Token Blacklist: Valkey (남은 유효시간만큼)
 * 
 * 2026년 Best Practice:
 * - Refresh Token Rotation
 * - 탈취 감지 (재사용 시 모든 토큰 폐기)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    
    // Key Prefix
    private static final String REFRESH_PREFIX = "rt:";           // refresh token
    private static final String BLACKLIST_PREFIX = "bl:";         // blacklist
    private static final String USER_REFRESH_PREFIX = "urt:";     // user -> refresh token mapping
    
    // TTL 설정
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);  // ✅ 2주
    
    /**
     * Refresh Token 생성 및 저장
     * 
     * @param userId 사용자 ID
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(String userId) {
        String refreshToken = generateRefreshToken();
        String refreshKey = REFRESH_PREFIX + refreshToken;
        String userKey = USER_REFRESH_PREFIX + userId;
        
        // 기존 Refresh Token이 있으면 삭제 (Rotation)
        String existingToken = redisTemplate.opsForValue().get(userKey);
        if (existingToken != null) {
            redisTemplate.delete(REFRESH_PREFIX + existingToken);
            log.debug("기존 Refresh Token 삭제 (Rotation): userId={}", userId);
        }
        
        // 새 Refresh Token 저장
        redisTemplate.opsForValue().set(refreshKey, userId, REFRESH_TOKEN_TTL);
        redisTemplate.opsForValue().set(userKey, refreshToken, REFRESH_TOKEN_TTL);
        
        log.info("Refresh Token 생성: userId={}, ttl={}일", userId, REFRESH_TOKEN_TTL.toDays());
        return refreshToken;
    }
    
    /**
     * Refresh Token 검증
     * 
     * @param refreshToken Refresh Token
     * @return userId (유효하면), null (유효하지 않으면)
     */
    public String validateRefreshToken(String refreshToken) {
        String key = REFRESH_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(key);
        
        if (userId == null) {
            log.warn("유효하지 않은 Refresh Token: {}", maskToken(refreshToken));
            return null;
        }
        
        log.debug("Refresh Token 검증 성공: userId={}", userId);
        return userId;
    }
    
    /**
     * Refresh Token Rotation
     * 새 토큰 발급 + 기존 토큰 삭제
     * 
     * @param oldRefreshToken 기존 Refresh Token
     * @return [newRefreshToken, userId] 또는 null (유효하지 않으면)
     */
    public String[] rotateRefreshToken(String oldRefreshToken) {
        String oldKey = REFRESH_PREFIX + oldRefreshToken;
        String userId = redisTemplate.opsForValue().get(oldKey);
        
        if (userId == null) {
            log.warn("Refresh Token Rotation 실패 - 유효하지 않은 토큰: {}", 
                    maskToken(oldRefreshToken));
            return null;
        }
        
        // 기존 토큰 삭제
        redisTemplate.delete(oldKey);
        
        // 새 토큰 생성
        String newRefreshToken = createRefreshToken(userId);
        
        log.info("Refresh Token Rotation 완료: userId={}", userId);
        return new String[]{newRefreshToken, userId};
    }
    
    /**
     * Access Token 블랙리스트 등록 (로그아웃)
     * 
     * @param accessToken Access Token
     * @param expirationMillis 남은 유효시간 (ms)
     */
    public void blacklistAccessToken(String accessToken, long expirationMillis) {
        String key = BLACKLIST_PREFIX + accessToken;
        Duration ttl = Duration.ofMillis(expirationMillis);
        
        redisTemplate.opsForValue().set(key, "logout", ttl);
        log.info("Access Token 블랙리스트 등록: ttl={}초", ttl.getSeconds());
    }
    
    /**
     * Access Token 블랙리스트 확인
     * 
     * @param accessToken Access Token
     * @return true (블랙리스트에 있음), false (정상)
     */
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken));
    }
    
    /**
     * 사용자의 모든 토큰 삭제 (로그아웃, 탈취 의심 시)
     * 
     * @param userId 사용자 ID
     */
    public void revokeAllTokens(String userId) {
        String userKey = USER_REFRESH_PREFIX + userId;
        String refreshToken = redisTemplate.opsForValue().get(userKey);
        
        if (refreshToken != null) {
            redisTemplate.delete(REFRESH_PREFIX + refreshToken);
            redisTemplate.delete(userKey);
            log.info("사용자 모든 토큰 삭제: userId={}", userId);
        }
    }
    
    /**
     * Refresh Token 삭제 (단일)
     * 
     * @param refreshToken Refresh Token
     */
    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(key);
        
        if (userId != null) {
            redisTemplate.delete(key);
            redisTemplate.delete(USER_REFRESH_PREFIX + userId);
            log.debug("Refresh Token 삭제: userId={}", userId);
        }
    }
    
    // ==================== Private Methods ====================
    
    /**
     * Refresh Token 생성 (Secure Random)
     */
    private String generateRefreshToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }
    
    /**
     * 토큰 마스킹 (로그용)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}
