package com.sungbok.church.service;

import com.sungbok.church.config.R2Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

/**
 * Cloudflare R2 Storage Service
 * 
 * 파일 업로드/다운로드/삭제를 담당
 * AWS SDK v2 최신 문법 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class R2StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final R2Config r2Config;
    private final Tika tika = new Tika();

    /**
     * MultipartFile을 R2에 업로드
     * 
     * @param file 업로드할 파일
     * @param folder 폴 경로 (예: "notices", "images")
     * @return 저장된 파일 URL (publicUrl 기반)
     */
    public String uploadFile(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String key = String.format("%s/%s.%s", folder, uuid, extension);

        try {
            // 파일 MIME 타입 감지
            String contentType = tika.detect(file.getInputStream());
            if (contentType == null) {
                contentType = file.getContentType();
            }

            // R2에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Config.getBucket())
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Public URL 반환
            String fileUrl = String.format("%s/%s", r2Config.getPublicUrl(), key);
            log.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;

        } catch (IOException e) {
            log.error("Failed to upload file: {}", originalFilename, e);
            throw new RuntimeException("파일 업로드 실패: " + originalFilename, e);
        }
    }

    /**
     * 파일 삭제
     * 
     * @param fileUrl 삭제할 파일 URL (publicUrl 기반)
     */
    public void deleteFile(String fileUrl) {
        try {
            // URL에서 key 추출
            String key = extractKeyFromUrl(fileUrl);
            
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(r2Config.getBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("File deleted successfully: {}", fileUrl);

        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            throw new RuntimeException("파일 삭제 실패: " + fileUrl, e);
        }
    }

    /**
     * 서명된 업로드 URL 생성 (직접 업로드용)
     * 클라이언트가 직접 R2에 업로드할 수 있는 임시 URL
     * 
     * @param key 파일 키
     * @param contentType MIME 타입
     * @param expiresInMinutes URL 유효 시간(분)
     * @return 서명된 PUT URL
     */
    public URL generatePresignedUploadUrl(String key, String contentType, int expiresInMinutes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(r2Config.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiresInMinutes))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url();
    }

    /**
     * 서명된 다운로드 URL 생성 (임시 접근용)
     * Private 파일에 대한 임시 접근 URL
     * 
     * @param fileUrl 파일 URL
     * @param expiresInMinutes URL 유효 시간(분)
     * @return 서명된 GET URL
     */
    public URL generatePresignedDownloadUrl(String fileUrl, int expiresInMinutes) {
        String key = extractKeyFromUrl(fileUrl);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(r2Config.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiresInMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url();
    }

    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(r2Config.getBucket())
                    .key(key)
                    .build();

            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking file existence: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * URL에서 R2 key 추출
     * 예: https://cdn.sungbok.church/notices/uuid.pdf → notices/uuid.pdf
     */
    private String extractKeyFromUrl(String fileUrl) {
        String publicUrl = r2Config.getPublicUrl();
        if (fileUrl.startsWith(publicUrl)) {
            return fileUrl.substring(publicUrl.length() + 1); // +1 for leading slash
        }
        // fallback: 마지막 슬래시 이후 경로 추출
        return fileUrl.substring(fileUrl.indexOf("/") + 1);
    }
}
