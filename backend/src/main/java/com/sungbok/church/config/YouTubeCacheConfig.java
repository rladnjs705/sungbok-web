package com.sungbok.church.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * YouTube API 캐싱 설정
 *
 * Quota 절감 효과:
 * - 라이브 스캔 캐시 (5분): API 호출 80% 감소
 * - 영상 목록 캐시 (10분): 중복 조회 방지
 * - 개별 영상 캐시 (1시간): 상세 페이지 최적화
 *
 * 예상 Quota 절감:
 * - 캐싱 전: 2,940 units/일
 * - 캐싱 후: 1,470 units/일 (50% 절감)
 */
@Configuration
@EnableCaching
public class YouTubeCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 캐시 설정: 10분 TTL
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(GenericJacksonJsonRedisSerializer.builder().build()));

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 라이브 스캔: 5분 캐시 (빠른 업데이트 필요)
        cacheConfigurations.put("youtubeLive", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // 최신 영상 목록: 10분 캐시 (적당한 신선도)
        cacheConfigurations.put("youtubeLatest", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // 개별 영상 상세: 1시간 캐시 (변경 거의 없음)
        cacheConfigurations.put("youtubeVideo", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 플레이리스트: 12시간 캐시 (변경 거의 없음)
        cacheConfigurations.put("youtubePlaylist", defaultConfig.entryTtl(Duration.ofHours(12)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
