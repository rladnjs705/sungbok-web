package com.sungbok.church.r2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest
public class R2ConnectionTest {

    @Autowired
    private S3Client s3Client;

    @Test
    public void testR2Connection() {
        try {
            System.out.println("=== R2 연결 테스트 시작 ===");
            
            // 1. 버킷 리스트 조회
            System.out.println("1. 버킷 리스트 조회...");
            ListBucketsResponse buckets = s3Client.listBuckets();
            System.out.println("   버킷 수: " + buckets.buckets().size());
            buckets.buckets().forEach(b -> System.out.println("   - " + b.name()));
            
            // 2. 버킷 존재 확인
            String bucketName = "sungbok-church-files";
            System.out.println("2. 버킷 '" + bucketName + "' 존재 확인...");
            HeadBucketResponse headResponse = s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("   버킷 접근 성공!");
            
            // 3. 테스트 파일 업로드
            String testKey = "test/r2-connection-test-" + UUID.randomUUID().toString().substring(0, 8) + ".txt";
            String testContent = "R2 Connection Test at " + Instant.now().toString();
            
            System.out.println("3. 테스트 파일 업로드: " + testKey);
            PutObjectResponse putResponse = s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .contentType("text/plain")
                    .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(testContent.getBytes(StandardCharsets.UTF_8)));
            System.out.println("   업로드 성공! ETag: " + putResponse.eTag());
            
            // 4. 업로드된 파일 조회
            System.out.println("4. 업로드된 파일 조회...");
            GetObjectResponse getResponse = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build()).response();
            System.out.println("   파일 크기: " + getResponse.contentLength() + " bytes");
            
            // 5. 파일 삭제 (정리)
            System.out.println("5. 테스트 파일 삭제...");
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build());
            System.out.println("   삭제 성공!");
            
            System.out.println("=== R2 연결 테스트 성공 ===");
        } catch (Exception e) {
            System.err.println("R2 연결 테스트 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
