package com.sungbok.church.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * YouTube API Configuration
 * YouTube Data API v3 설정 및 RestTemplate 빈 생성
 */
@Configuration
@ConfigurationProperties(prefix = "youtube")
@Getter
public class YouTubeConfig {

    private Api api = new Api();
    private Sync sync = new Sync();
    private Quota quota = new Quota();

    @Getter
    public static class Api {
        private String key;
        private String channelId;
        private String baseUrl;
        private int maxResults = 50;

        public void setKey(String key) {
            this.key = key;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public void setMaxResults(int maxResults) {
            this.maxResults = maxResults;
        }
    }

    @Getter
    public static class Sync {
        private boolean enabled = true;
        private long liveCheckInterval = 60000;
        private String videoSyncCron;
        private String playlistSyncCron;

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setLiveCheckInterval(long liveCheckInterval) {
            this.liveCheckInterval = liveCheckInterval;
        }

        public void setVideoSyncCron(String videoSyncCron) {
            this.videoSyncCron = videoSyncCron;
        }

        public void setPlaylistSyncCron(String playlistSyncCron) {
            this.playlistSyncCron = playlistSyncCron;
        }
    }

    @Getter
    public static class Quota {
        private int dailyLimit = 10000;
        private int warningThreshold = 8000;

        public void setDailyLimit(int dailyLimit) {
            this.dailyLimit = dailyLimit;
        }

        public void setWarningThreshold(int warningThreshold) {
            this.warningThreshold = warningThreshold;
        }
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
