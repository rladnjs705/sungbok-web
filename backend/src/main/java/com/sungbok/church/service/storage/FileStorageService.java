package com.sungbok.church.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 파일 저장소 서비스 인터페이스
 * 구현체 교체 가능 (R2, LocalStack, Mock 등)
 */
public interface FileStorageService {

    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param folder 폴더 경로 (예: "notices/1")
     * @return 저장된 파일 URL
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일 URL
     */
    void deleteFile(String fileUrl);

    /**
     * 서명된 업로드 URL 생성 (직접 업로드용)
     * @param key 파일 키
     * @param contentType 콘텐츠 타입
     * @param expiresInMinutes 만료 시간(분)
     * @return 서명된 URL
     */
    URL generatePresignedUploadUrl(String key, String contentType, int expiresInMinutes);

    /**
     * 서명된 다운로드 URL 생성
     * @param fileUrl 파일 URL
     * @param expiresInMinutes 만료 시간(분)
     * @return 서명된 URL
     */
    URL generatePresignedDownloadUrl(String fileUrl, int expiresInMinutes);

    /**
     * 파일 존재 여부 확인
     * @param fileUrl 파일 URL
     * @return 존재 여부
     */
    boolean fileExists(String fileUrl);

    /**
     * 다중 파일 업로드
     * @param files 파일 목록
     * @param folder 폴더 경로
     * @return 저장된 파일 URL 목록
     */
    default List<String> uploadFiles(List<MultipartFile> files, String folder) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> uploadFile(file, folder))
                .collect(Collectors.toList());
    }
}
