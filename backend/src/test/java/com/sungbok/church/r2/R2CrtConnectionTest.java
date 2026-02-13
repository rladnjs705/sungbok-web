package com.sungbok.church.r2;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class R2CrtConnectionTest {

    private static final String ENDPOINT = "https://ae0ae9316f064cf9a5a10592af41411f.r2.cloudflarestorage.com";
    private static final String ACCESS_KEY = "4979f67ac8f333a643115e1b471ccc64";
    private static final String SECRET_KEY = "b997a9df3211f67d16668e0a5c5bb0bcb02f3ba1b6f90c86c51f8e7b94b5f750";
    private static final String BUCKET = "sungbok-church-files";

    @Test
    public void testCrtR2Connection() {
        System.out.println("=== R2 CRT Client 연결 테스트 시작 ===");
        System.out.println("TLS Protocol: " + System.getProperty("jdk.tls.client.protocols", "default (TLS 1.3)"));
        System.out.println("HTTP Client: AWS CRT");
        
        S3Client s3Client = S3Client.builder()
                .httpClient(AwsCrtHttpClient.builder()
                        .maxConcurrency(100)
                        .connectionTimeout(Duration.ofSeconds(10))
                        .build())
                .endpointOverride(URI.create(ENDPOINT))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
                ))
                .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

        try {
            // 1. 버킷 리스트 조회
            System.out.println("\n1. 버킷 리스트 조회...");
            ListBucketsResponse buckets = s3Client.listBuckets();
            System.out.println("   버킷 수: " + buckets.buckets().size());
            buckets.buckets().forEach(b -> System.out.println("   - " + b.name()));

            // 2. 버킷 접근 확인
            System.out.println("\n2. 버킷 '" + BUCKET + "' 접근 확인...");
            HeadBucketResponse headResponse = s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(BUCKET)
                    .build());
            System.out.println("   버킷 접근 성공!");

            // 3. 테스트 파일 업로드
            String testKey = "test/r2-crt-test-" + UUID.randomUUID().toString().substring(0, 8) + ".txt";
            String testContent = "R2 CRT Test at " + Instant.now().toString();

            System.out.println("\n3. 테스트 파일 업로드: " + testKey);
            PutObjectResponse putResponse = s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET)
                            .key(testKey)
                            .contentType("text/plain")
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(testContent.getBytes(StandardCharsets.UTF_8)));
            System.out.println("   업로드 성공! ETag: " + putResponse.eTag());

            // 4. 파일 조회
            System.out.println("\n4. 업로드된 파일 조회...");
            GetObjectResponse getResponse = s3Client.getObject(GetObjectRequest.builder()
                            .bucket(BUCKET)
                            .key(testKey)
                            .build()).response();
            System.out.println("   파일 크기: " + getResponse.contentLength() + " bytes");

            // 5. 파일 삭제
            System.out.println("\n5. 테스트 파일 삭제...");
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(testKey)
                    .build());
            System.out.println("   삭제 성공!");

            System.out.println("\n=== R2 CRT Client 연결 테스트 성공 ===");
        } catch (Exception e) {
            System.err.println("\nR2 CRT Client 연결 테스트 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            s3Client.close();
        }
    }
}
