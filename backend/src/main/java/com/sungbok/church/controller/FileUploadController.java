package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.FileUpload;
import com.sungbok.church.dto.upload.PresignUrlRequest;
import com.sungbok.church.dto.upload.PresignUrlResponse;
import com.sungbok.church.dto.upload.UploadCompleteRequest;
import com.sungbok.church.dto.upload.UploadCompleteResponse;
import com.sungbok.church.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

/**
 * 파일 업로드 Controller
 * 
 * Pre-signed URL 기반 파일 업로드 관리 REST API
 * - 클라이언트는 서버에서 Pre-signed URL을 발급받아 직접 R2에 업로드
 * - 업로드 완료 후 서버에 확인 요청
 */
@Tag(name = "파일 업로드", description = "Pre-signed URL 기반 파일 업로드 API")
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Pre-signed URL 발급
     * 클라이언트가 직접 R2에 파일을 업로드할 수 있는 임시 URL 발급
     */
    @Operation(
        summary = "Pre-signed URL 발급", 
        description = "클리이언트가 직접 R2에 파일을 업로드할 수 있는 임시 URL을 발급합니다. URL은 10분간 유효합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "URL 발급 성공", 
            content = @Content(schema = @Schema(implementation = PresignUrlResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/presign")
    public ResponseEntity<PresignUrlResponse> generatePresignedUrl(
            @Parameter(description = "파일 업로드 정보") 
            @Valid @RequestBody PresignUrlRequest request,
            @Parameter(description = "인증된 사용자", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userId = getUserId(userDetails);
        log.info("Generating pre-signed URL for user: {}, filename: {}", userId, request.getFilename());

        // Pre-signed URL 생성 및 DB 저장
        FileUpload fileUpload = fileUploadService.generatePresignedUrl(request, userId);
        
        // 실제 Pre-signed URL 생성 (10분 만료)
        URL presignedUrl = fileUploadService.generatePresignedUrlForKey(
            fileUpload.getStorageKey(), 
            request.getContentType()
        );

        PresignUrlResponse response = PresignUrlResponse.from(fileUpload, presignedUrl.toString());
        log.info("Pre-signed URL generated: uploadId={}, key={}", response.getUploadId(), response.getKey());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 파일 업로드 완료 확인
     * 클라이언트가 R2에 파일 업로드를 완료한 후 서버에 검증 요청
     */
    @Operation(
        summary = "업로드 완료 확인", 
        description = "클리이언트가 R2에 파일 업로드를 완료한 후 서버에 검증을 요청합니다. 서버는 R2에서 파일 존재 및 체크섬을 검증합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검증 결과", 
            content = @Content(schema = @Schema(implementation = UploadCompleteResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
        @ApiResponse(responseCode = "404", description = "업로드 정보를 찾을 수 없음", content = @Content)
    })
    @PostMapping("/{uploadId}/complete")
    public ResponseEntity<UploadCompleteResponse> confirmUpload(
            @Parameter(description = "업로드 ID") 
            @PathVariable Long uploadId,
            @Parameter(description = "업로드 완료 정보") 
            @Valid @RequestBody UploadCompleteRequest request,
            @Parameter(description = "인증된 사용자", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userId = getUserId(userDetails);
        log.info("Confirming upload: uploadId={}, user={}", uploadId, userId);

        UploadCompleteResponse response = fileUploadService.confirmUpload(uploadId, request);
        
        if (response.getSuccess()) {
            log.info("Upload confirmed successfully: uploadId={}, fileUrl={}", 
                uploadId, response.getFileUrl());
        } else {
            log.warn("Upload confirmation failed: uploadId={}, error={}", 
                uploadId, response.getError());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 파일 존재 여부 확인 (HEAD 요청)
     * R2에 파일이 실제로 존재하는지 확인
     */
    @Operation(
        summary = "파일 존재 여부 확인", 
        description = "R2 저장소에 파일이 실제로 존재하는지 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "파일 존재함"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/verify/{key}")
    public ResponseEntity<Void> verifyFile(
            @Parameter(description = "저장소 키 (예: notices/uuid.pdf)") 
            @PathVariable String key) {
        
        boolean exists = fileUploadService.verifyFileInStorage(key);
        
        if (exists) {
            log.debug("File verified in storage: {}", key);
            return ResponseEntity.ok().build();
        } else {
            log.warn("File not found in storage: {}", key);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 사용자 ID 추출
     * TODO: 테스트 후 인증 필수로 변경
     */
    private String getUserId(UserDetails userDetails) {
        return userDetails != null ? userDetails.getUsername() : "anonymous";
    }
}
