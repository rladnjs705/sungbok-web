package com.sungbok.church.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

/**
 * Cloudflare R2 Configuration - AWS CRT HTTP Client 버전
 *
 * TLS 1.3 완벽 지원, 더 나은 성능
 * 사용하려면 --spring.profiles.active=crt 추가
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "r2")
@Getter
@Setter
@Profile("crt")  // crt 프로파일에서만 활성화
public class R2ConfigCrt {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String publicUrl;

    /**
     * AWS CRT HTTP Client - 더 나은 TLS 1.3 지원
     */
    @Bean
    public SdkHttpClient r2HttpClient() {
        log.info("Initializing R2 HTTP Client with AWS CRT (TLS 1.3 optimized)");
        log.info("R2 Endpoint: {}", endpoint);

        return AwsCrtHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public S3Client s3Client(SdkHttpClient r2HttpClient) {
        return S3Client.builder()
                .httpClient(r2HttpClient)
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        // S3Presigner는 기본 HTTP 클라이언트 사용
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
    }
}
