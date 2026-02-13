package com.sungbok.church.service.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 파일 저장소 통합 테스트
 * InMemoryFileStorageService 사용 (빠른 테스트)
 */
@SpringBootTest
@ActiveProfiles("test")
public class FileStorageIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public FileStorageService fileStorageService() {
            return new InMemoryFileStorageService();
        }
    }

    @Autowired
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        if (fileStorageService instanceof InMemoryFileStorageService) {
            ((InMemoryFileStorageService) fileStorageService).clear();
        }
    }

    @Test
    @DisplayName("파일 업로드 및 조회 통합 테스트")
    void uploadAndRetrieveFile_Success() throws IOException {
        // Given
        byte[] content = "Integration test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "integration.txt", "text/plain", content
        );

        // When
        String fileUrl = fileStorageService.uploadFile(file, "integration-folder");

        // Then
        assertThat(fileUrl).isNotNull();
        assertThat(fileStorageService.fileExists(fileUrl)).isTrue();
        
        // 파일 내용 확인
        if (fileStorageService instanceof InMemoryFileStorageService) {
            byte[] retrievedContent = ((InMemoryFileStorageService) fileStorageService).getFile(fileUrl);
            assertThat(retrievedContent).isEqualTo(content);
        }
    }

    @Test
    @DisplayName("파일 삭제 통합 테스트")
    void deleteFile_Success() throws IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "delete-test.txt", "text/plain", "Delete me".getBytes()
        );
        String fileUrl = fileStorageService.uploadFile(file, "test-folder");
        assertThat(fileStorageService.fileExists(fileUrl)).isTrue();

        // When
        fileStorageService.deleteFile(fileUrl);

        // Then
        assertThat(fileStorageService.fileExists(fileUrl)).isFalse();
    }

    @Test
    @DisplayName("대용량 파일 업로드 통합 테스트")
    void uploadLargeFile_Success() throws IOException {
        // Given
        byte[] largeContent = new byte[5 * 1024 * 1024]; // 5MB
        new Random().nextBytes(largeContent);
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.bin", "application/octet-stream", largeContent
        );

        // When
        String fileUrl = fileStorageService.uploadFile(file, "large-files");

        // Then
        assertThat(fileUrl).isNotNull();
        assertThat(fileStorageService.fileExists(fileUrl)).isTrue();
    }

    @Test
    @DisplayName("서명된 URL 생성 통합 테스트")
    void generatePresignedUrl_Success() throws IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "presigned.txt", "text/plain", "Presigned content".getBytes()
        );
        String fileUrl = fileStorageService.uploadFile(file, "test-folder");

        // When
        URL downloadUrl = fileStorageService.generatePresignedDownloadUrl(fileUrl, 10);

        // Then
        assertThat(downloadUrl).isNotNull();
    }
}
