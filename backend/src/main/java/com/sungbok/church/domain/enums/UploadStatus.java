package com.sungbok.church.domain.enums;

/**
 * 파일 업로드 상태
 */
public enum UploadStatus {
    /**
     * 초기 상태
     */
    PENDING,

    /**
     * Pre-signed URL 발급됨
     */
    URL_ISSUED,

    /**
     * 클라이언트가 업로드 완료 알림
     */
    UPLOADED,

    /**
     * 서버에서 검증 완료
     */
    VERIFIED,

    /**
     * 업로드 실패
     */
    FAILED
}
