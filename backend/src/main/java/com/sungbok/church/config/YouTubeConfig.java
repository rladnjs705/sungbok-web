package com.sungbok.church.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * YouTube API Configuration
 * YouTube Data API v3 설정 및 RestTemplate 빈 생성
 *
 * 개선 (2026-02-11):
 * - @Setter 클래스 레벨 적용으로 코드 간소화
 */
@Configuration
@ConfigurationProperties(prefix = "youtube")
@Getter
@Setter
public class YouTubeConfig {

    private Api api = new Api();
    private Sync sync = new Sync();
    private Quota quota = new Quota();

    @Getter
    @Setter
    public static class Api {
        private String key;
        private String channelId;
        private String baseUrl;
        private int maxResults = 50;
    }

    @Getter
    @Setter
    public static class Sync {
        private boolean enabled = true;
        private long liveCheckInterval = 60000;
        private String videoSyncCron = "0 0 * * * *";
        private String playlistSyncCron = "0 0 */6 * * *";
    }

    @Getter
    @Setter
    public static class Quota {
        private int dailyLimit = 10000;
        private int warningThreshold = 8000;
    }

    /**
     * RestTemplate 빈 생성
     * YouTube API 호출에 사용
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
