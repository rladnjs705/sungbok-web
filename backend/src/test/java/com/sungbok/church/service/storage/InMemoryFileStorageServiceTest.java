package com.sungbok.church.service.storage;

import com.sungbok.church.service.storage.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * In-Memory 파일 저장소 서비스 테스트
 * TDD: 빠른 피드백을 위한 단위 테스트
 */
class InMemoryFileStorageServiceTest {

    private InMemoryFileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new InMemoryFileStorageService();
    }

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFile_Success() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );

        // When
        String fileUrl = fileStorageService.uploadFile(file, "test-folder");

        // Then
        assertThat(fileUrl).isNotNull();
        assertThat(fileUrl).startsWith("memory://test-folder/");
        assertThat(fileStorageService.fileExists(fileUrl)).isTrue();
    }

    @Test
    @DisplayName("빈 파일 업로드 시 예외 발생")
    void uploadFile_EmptyFile_ThrowsException() {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]
        );

        // When & Then
        assertThatThrownBy(() -> fileStorageService.uploadFile(emptyFile, "test-folder"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비어있습니다");
    }

    @Test
    @DisplayName("null 파일 업로드 시 예외 발생")
    void uploadFile_NullFile_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> fileStorageService.uploadFile(null, "test-folder"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFile_Success() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );
        String fileUrl = fileStorageService.uploadFile(file, "test-folder");

        // When
        fileStorageService.deleteFile(fileUrl);

        // Then
        assertThat(fileStorageService.fileExists(fileUrl)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 파일 삭제 시 정상 처리")
    void deleteFile_NonExistentFile_NoException() {
        // When & Then
        fileStorageService.deleteFile("memory://test-folder/non-existent.txt");
        // 예외 발생하지 않음
    }

    @Test
    @DisplayName("다중 파일 업로드 성공")
    void uploadFiles_Success() {
        // Given
        MockMultipartFile file1 = new MockMultipartFile(
                "file1", "test1.txt", "text/plain", "Content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2", "test2.txt", "text/plain", "Content 2".getBytes()
        );

        // When
        List<String> fileUrls = fileStorageService.uploadFiles(
                Arrays.asList(file1, file2), "test-folder"
        );

        // Then
        assertThat(fileUrls).hasSize(2);
        assertThat(fileUrls).allMatch(url -> url.startsWith("memory://test-folder/"));
    }

    @Test
    @DisplayName("빈 리스트 업로드 시 빈 결과 반환")
    void uploadFiles_EmptyList_ReturnsEmptyList() {
        // When
        List<String> result = fileStorageService.uploadFiles(Collections.emptyList(), "test-folder");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null 리스트 업로드 시 빈 결과 반환")
    void uploadFiles_NullList_ReturnsEmptyList() {
        // When
        List<String> result = fileStorageService.uploadFiles(null, "test-folder");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("파일 내용 확인")
    void getFile_ContentMatches() throws IOException {
        // Given
        byte[] content = "Test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", content
        );

        // When
        String fileUrl = fileStorageService.uploadFile(file, "test-folder");
        byte[] retrievedContent = fileStorageService.getFile(fileUrl);

        // Then
        assertThat(retrievedContent).isEqualTo(content);
    }

    @Test
    @DisplayName("서명된 업로드 URL 생성")
    void generatePresignedUploadUrl_Success() {
        // When
        URL url = fileStorageService.generatePresignedUploadUrl(
                "test-key", "text/plain", 10
        );

        // Then
        assertThat(url).isNotNull();
        assertThat(url.toString()).contains("test-key");
    }

    @Test
    @DisplayName("서명된 다운로드 URL 생성")
    void generatePresignedDownloadUrl_Success() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello".getBytes()
        );
        String fileUrl = fileStorageService.uploadFile(file, "test-folder");

        // When
        URL url = fileStorageService.generatePresignedDownloadUrl(fileUrl, 10);

        // Then
        assertThat(url).isNotNull();
    }
}
