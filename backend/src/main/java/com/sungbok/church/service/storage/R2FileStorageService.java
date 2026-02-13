package com.sungbok.church.service.storage;

import com.sungbok.church.config.R2Config;
import com.sungbok.church.service.storage.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Profile;
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
 * Cloudflare R2 파일 저장소 서비스 구현
 * AWS SDK v2 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class R2FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final R2Config r2Config;
    private final Tika tika = new Tika();

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String key = String.format("%s/%s.%s", folder, uuid, extension);

        try {
            String contentType = detectContentType(file);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Config.getBucket())
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = String.format("%s/%s", r2Config.getPublicUrl(), key);
            log.info("File uploaded: {} ({} bytes)", fileUrl, file.getSize());

            return fileUrl;

        } catch (IOException e) {
            log.error("Failed to upload file: {}", originalFilename, e);
            throw new FileStorageException("파일 업로드 실패: " + originalFilename, e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(r2Config.getBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("File deleted: {}", fileUrl);

        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            throw new FileStorageException("파일 삭제 실패: " + fileUrl, e);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB 제한
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }

    private String detectContentType(MultipartFile file) throws IOException {
        String contentType = tika.detect(file.getInputStream());
        return contentType != null ? contentType : file.getContentType();
    }

    private String extractKeyFromUrl(String fileUrl) {
        String publicUrl = r2Config.getPublicUrl();
        if (fileUrl.startsWith(publicUrl)) {
            return fileUrl.substring(publicUrl.length() + 1); // 슬래시 제거
        }
        // fallback: 마지막 슬래시 이후를 키로 사용
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            return fileUrl.substring(lastSlashIndex + 1);
        }
        return fileUrl;
    }

    private String getExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
