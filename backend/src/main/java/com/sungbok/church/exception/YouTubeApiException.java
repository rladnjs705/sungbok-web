package com.sungbok.church.exception;

/**
 * YouTube API Exception
 * YouTube Data API 호출 중 발생하는 예외 처리
 */
public class YouTubeApiException extends RuntimeException {

    private final int statusCode;
    private final String errorType;

    public YouTubeApiException(String message) {
        super(message);
        this.statusCode = 500;
        this.errorType = "YOUTUBE_API_ERROR";
    }

    public YouTubeApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
        this.errorType = "YOUTUBE_API_ERROR";
    }

    public YouTubeApiException(String message, int statusCode, String errorType) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    public YouTubeApiException(String message, Throwable cause, int statusCode, String errorType) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorType() {
        return errorType;
    }

    /**
     * Quota 초과 예외
     */
    public static YouTubeApiException quotaExceeded(String message) {
        return new YouTubeApiException(message, 429, "QUOTA_EXCEEDED");
    }

    /**
     * 네트워크 타임아웃 예외
     */
    public static YouTubeApiException timeout(String message, Throwable cause) {
        return new YouTubeApiException(message, cause, 408, "TIMEOUT");
    }

    /**
     * 잘못된 API Key 예외
     */
    public static YouTubeApiException invalidApiKey(String message) {
        return new YouTubeApiException(message, 401, "INVALID_API_KEY");
    }

    /**
     * 리소스를 찾을 수 없음
     */
    public static YouTubeApiException notFound(String message) {
        return new YouTubeApiException(message, 404, "NOT_FOUND");
    }
}
