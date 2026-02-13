package com.sungbok.church.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

/**
 * Cloudflare R2 Configuration
 *
 * R2는 AWS S3 API와 호환됩니다.
 * Apache HTTP Client를 사용하며 TLS 1.3를 유지하면서 connection timeout 설정
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "r2")
@Getter
@Setter
public class R2Config {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String publicUrl;

    /**
     * 공유 HTTP 클라이언트 - 연결 풀 공유로 리소스 절약
     * Connection timeout 설정 (Cloudflare R2와의 연결 문제 해결)
     */
    @Bean
    public SdkHttpClient r2HttpClient() {
        log.info("Initializing R2 HTTP Client with Apache HTTP Client (TLS 1.3)");
        log.info("R2 Endpoint: {}", endpoint);

        return ApacheHttpClient.builder()
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(10))  // TCP 연결 타임아웃 (TLS handshake 포함)
                .socketTimeout(Duration.ofSeconds(30))      // 소켓 타임아웃
                .connectionAcquisitionTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public S3Client s3Client(SdkHttpClient r2HttpClient) {
        return S3Client.builder()
                .httpClient(r2HttpClient)
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto"))  // R2는 region 개념 없음
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)   // R2 필수
                        .chunkedEncodingEnabled(false)  // R2 권장
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        // S3Presigner는 별도의 HTTP 클라이언트 설정 없이 기본값 사용
        // 낮은 수준의 설정이 필요하면 PresignerHttpConfiguration 사용
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
