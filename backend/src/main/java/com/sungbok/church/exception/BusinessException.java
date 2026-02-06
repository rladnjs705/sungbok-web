package com.sungbok.church.exception;

/**
 * 비즈니스 로직 위반 시 발생하는 예외
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
