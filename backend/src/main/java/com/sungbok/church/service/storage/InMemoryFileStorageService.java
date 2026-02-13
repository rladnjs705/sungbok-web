package com.sungbok.church.service.storage;

import com.sungbok.church.service.storage.exception.FileStorageException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 테스트용 In-Memory 파일 저장소
 * TDD 초기 단계 및 개발 환경에서 사용
 */
@Service
@Profile("test")
public class InMemoryFileStorageService implements FileStorageService {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();
    private final Map<String, String> contentTypes = new ConcurrentHashMap<>();

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        
        try {
            String originalFilename = file.getOriginalFilename();
            String key = String.format("%s/%s_%s", folder, UUID.randomUUID(), originalFilename);
            storage.put(key, file.getBytes());
            contentTypes.put(key, file.getContentType());
            return "memory://" + key;
        } catch (IOException e) {
            throw new FileStorageException("파일 업로드 실패", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        String key = extractKey(fileUrl);
        storage.remove(key);
        contentTypes.remove(key);
    }

    @Override
    public URL generatePresignedUploadUrl(String key, String contentType, int expiresInMinutes) {
        try {
            return new URL("http://localhost:4566/" + key);
        } catch (MalformedURLException e) {
            throw new FileStorageException("URL 생성 실패", e);
        }
    }

    @Override
    public URL generatePresignedDownloadUrl(String fileUrl, int expiresInMinutes) {
        return generatePresignedUploadUrl(extractKey(fileUrl), null, expiresInMinutes);
    }

    @Override
    public boolean fileExists(String fileUrl) {
        return storage.containsKey(extractKey(fileUrl));
    }

    // 테스트 헬퍼 메서드
    public void clear() {
        storage.clear();
        contentTypes.clear();
    }

    public byte[] getFile(String fileUrl) {
        return storage.get(extractKey(fileUrl));
    }

    public String getContentType(String fileUrl) {
        return contentTypes.get(extractKey(fileUrl));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
    }

    private String extractKey(String fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        return fileUrl.replace("memory://", "");
    }
}
