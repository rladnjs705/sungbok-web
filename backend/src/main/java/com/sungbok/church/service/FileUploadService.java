package com.sungbok.church.service;

import com.sungbok.church.config.R2Config;
import com.sungbok.church.domain.entity.FileUpload;
import com.sungbok.church.domain.enums.UploadStatus;
import com.sungbok.church.domain.repository.FileUploadRepository;
import com.sungbok.church.dto.upload.PresignUrlRequest;
import com.sungbok.church.dto.upload.UploadCompleteRequest;
import com.sungbok.church.dto.upload.UploadCompleteResponse;
import com.sungbok.church.exception.BusinessException;
import com.sungbok.church.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 파일 업로드 서비스
 * 
 * Pre-signed URL 기반 파일 업로드 관리
 * - Pre-signed URL 발급
 * - 업로드 완료 확인 및 검증
 * - R2 Storage 연동
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final R2Config r2Config;

    private static final int PRESIGNED_URL_EXPIRY_MINUTES = 10;

    /**
     * Pre-signed URL 발급
     * 
     * @param request 파일 업로드 요청 정보
     * @param userId 업로드 사용자 ID
     * @return Pre-signed URL 정보
     */
    @Transactional
    public FileUpload generatePresignedUrl(PresignUrlRequest request, String userId) {
        // 체크섬 검증 (제공된 경우)
        if (request.getChecksum() != null && !request.getChecksum().isBlank()) {
            validateChecksum(request.getChecksum());
        }

        // 파일 확장자 추출 및 저장소 키 생성
        String extension = FilenameUtils.getExtension(request.getFilename());
        String uuid = UUID.randomUUID().toString();
        String storageKey = String.format("%s/%s.%s", request.getFolder(), uuid, extension);

        // 파일 URL 생성
        String fileUrl = String.format("%s/%s", r2Config.getPublicUrl(), storageKey);
        LocalDateTime urlExpiresAt = LocalDateTime.now().plusMinutes(PRESIGNED_URL_EXPIRY_MINUTES);

        // FileUpload 엔티티 생성 및 저장 (PENDING 상태)
        FileUpload fileUpload = FileUpload.builder()
            .status(UploadStatus.PENDING)
            .originalFilename(request.getFilename())
            .storageKey(storageKey)
            .fileSize(request.getFileSize())
            .contentType(request.getContentType())
            .checksum(request.getChecksum())
            .uploadedBy(userId)
            .fileUrl(fileUrl)
            .urlExpiresAt(urlExpiresAt)
            .build();

        FileUpload savedUpload = fileUploadRepository.save(fileUpload);
        log.info("FileUpload created with ID: {} for user: {}", savedUpload.getId(), userId);

        // Pre-signed URL 생성
        URL presignedUrl = createPresignedUrl(storageKey, request.getContentType());
        log.info("Pre-signed URL generated for key: {}, expires at: {}", storageKey, urlExpiresAt);

        // 상태 업데이트: URL_ISSUED
        savedUpload.setStatus(UploadStatus.URL_ISSUED);
        fileUploadRepository.save(savedUpload);

        // 임시로 presignedUrl을 저장 (Controller에서 사용)
        savedUpload.setFileUrl(fileUrl); // 원래 URL로 복원
        
        // 응답용 객체 생성 (presignedUrl 포함을 위해 임시 필드 사용)
        return savedUpload;
    }

    /**
     * Pre-signed URL 생성
     */
    private URL createPresignedUrl(String key, String contentType) {
        software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest = 
            software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                .bucket(r2Config.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRY_MINUTES))
            .putObjectRequest(putObjectRequest)
            .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url();
    }

    /**
     * 업로드 완료 확인 및 검증
     * 
     * @param uploadId 업로드 ID
     * @param request 업로드 완료 요청
     * @return 업로드 완료 응답
     */
    @Transactional
    public UploadCompleteResponse confirmUpload(Long uploadId, UploadCompleteRequest request) {
        // 파일 업로드 정보 조회
        FileUpload fileUpload = fileUploadRepository.findById(uploadId)
            .orElseThrow(() -> new ResourceNotFoundException("파일 업로드 정보를 찾을 수 없습니다: " + uploadId));

        // 상태 확인 (URL_ISSUED 또는 UPLOADED 상태여야 함)
        if (fileUpload.getStatus() != UploadStatus.URL_ISSUED && 
            fileUpload.getStatus() != UploadStatus.UPLOADED) {
            throw new BusinessException("유효하지 않은 업로드 상태입니다: " + fileUpload.getStatus());
        }

        // 저장소 키 일치 확인
        if (!fileUpload.getStorageKey().equals(request.getKey())) {
            throw new BusinessException("저장소 키가 일치하지 않습니다");
        }

        // R2에서 파일 존재 확인
        boolean exists = verifyFileInStorage(request.getKey());
        if (!exists) {
            log.warn("File not found in storage: {}", request.getKey());
            fileUpload.setStatus(UploadStatus.FAILED);
            fileUploadRepository.save(fileUpload);
            return UploadCompleteResponse.failure("파일이 저장소에 존재하지 않습니다");
        }

        // 체크섬 검증 (제공된 경우)
        if (request.getChecksum() != null && !request.getChecksum().isBlank()) {
            if (fileUpload.getChecksum() != null && 
                !fileUpload.getChecksum().equals(request.getChecksum())) {
                log.warn("Checksum mismatch for upload: {}", uploadId);
                fileUpload.setStatus(UploadStatus.FAILED);
                fileUploadRepository.save(fileUpload);
                return UploadCompleteResponse.failure("체크섬이 일치하지 않습니다");
            }
        }

        // 파일 메타데이터 확인 (선택적)
        try {
            HeadObjectResponse headResponse = getFileMetadata(request.getKey());
            log.info("File metadata retrieved - Size: {}, Content-Type: {}", 
                headResponse.contentLength(), headResponse.contentType());
            
            // 파일 크기 검증 (선택적)
            if (headResponse.contentLength() != fileUpload.getFileSize()) {
                log.warn("File size mismatch. Expected: {}, Actual: {}", 
                    fileUpload.getFileSize(), headResponse.contentLength());
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve file metadata: {}", e.getMessage());
        }

        // 상태 업데이트: VERIFIED
        fileUpload.setStatus(UploadStatus.VERIFIED);
        fileUpload.setUploadedAt(LocalDateTime.now());
        fileUpload.setVerifiedAt(LocalDateTime.now());
        fileUploadRepository.save(fileUpload);

        log.info("Upload confirmed and verified: {}", uploadId);
        return UploadCompleteResponse.success(fileUpload);
    }

    /**
     * R2 저장소에서 파일 존재 여부 확인
     * 
     * @param key 저장소 키
     * @return 파일 존재 여부
     */
    public boolean verifyFileInStorage(String key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(r2Config.getBucket())
                .key(key)
                .build();

            s3Client.headObject(headRequest);
            log.debug("File exists in storage: {}", key);
            return true;
        } catch (NoSuchKeyException e) {
            log.debug("File not found in storage: {}", key);
            return false;
        } catch (Exception e) {
            log.error("Error checking file existence: {}", key, e);
            return false;
        }
    }

    /**
     * R2 저장소에서 파일 메타데이터 조회
     */
    private HeadObjectResponse getFileMetadata(String key) {
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
            .bucket(r2Config.getBucket())
            .key(key)
            .build();

        return s3Client.headObject(headRequest);
    }

    /**
     * 체크섬 형식 검증
     */
    private void validateChecksum(String checksum) {
        // SHA-256 체크섬은 64자의 hex 문자열
        if (!checksum.matches("^[a-fA-F0-9]+$")) {
            throw new BusinessException("체크섬은 hex 문자열이어야 합니다");
        }
        if (checksum.length() != 64 && checksum.length() != 32 && checksum.length() != 40) {
            log.warn("Unexpected checksum length: {}", checksum.length());
        }
    }

    /**
     * Pre-signed URL 생성 (public 메서드 - Controller에서 사용)
     * 
     * @param storageKey 저장소 키
     * @param contentType Content-Type
     * @return Pre-signed URL
     */
    public URL generatePresignedUrlForKey(String storageKey, String contentType) {
        return createPresignedUrl(storageKey, contentType);
    }
}
