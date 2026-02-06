package com.sungbok.church;

import com.sungbok.church.config.YouTubeConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(YouTubeConfig.class)
@EnableScheduling
@EnableCaching
public class SungbokChurchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SungbokChurchApplication.class, args);
    }
}
