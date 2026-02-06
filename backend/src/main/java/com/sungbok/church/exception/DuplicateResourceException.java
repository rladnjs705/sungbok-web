package com.sungbok.church.exception;

/**
 * 중복된 리소스가 존재할 때 발생하는 예외
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
