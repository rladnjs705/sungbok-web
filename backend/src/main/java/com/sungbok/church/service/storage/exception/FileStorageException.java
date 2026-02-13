package com.sungbok.church.service.storage.exception;

/**
 * 파일 저장소 예외
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
