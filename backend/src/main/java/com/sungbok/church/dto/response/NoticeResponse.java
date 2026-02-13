package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.entity.NoticeAttachment;
import com.sungbok.church.domain.enums.NoticeCategory;
import com.sungbok.church.dto.projection.NoticeProjectionDto;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Notice 조회 Response DTO
 * 
 * Cloudflare R2 연동 (첨부 파일 지원)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {

    private Long id;
    private NoticeCategory category;
    private String title;
    private String content;
    private String author;
    private Boolean isPinned;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 프론트엔드용 추가 필드
    private String excerpt;                 // content의 요약 (200자)
    private String imageUrl;                // 본문 내 첫 번째 이미지 URL
    private String date;                    // YYYY.MM.DD 형식
    private List<AttachmentResponse> attachments; // 첨부 파일 목록

    /**
     * Entity -> Response DTO 변환 (기본)
     */
    public static NoticeResponse from(Notice notice) {
        return from(notice, null);
    }

    /**
     * Entity -> Response DTO 변환 (첨부 파일 포함)
     */
    public static NoticeResponse from(Notice notice, List<NoticeAttachment> attachments) {
        String excerpt = generateExcerpt(notice.getContent());
        String imageUrl = extractFirstImageUrl(notice.getContent());
        String date = formatDate(notice.getPublishedAt());

        List<AttachmentResponse> attachmentResponses = attachments != null
            ? attachments.stream()
                .map(AttachmentResponse::from)
                .collect(Collectors.toList())
            : null;

        return NoticeResponse.builder()
            .id(notice.getId())
            .category(notice.getCategory())
            .title(notice.getTitle())
            .content(notice.getContent())
            .author(notice.getAuthor())
            .isPinned(notice.getIsPinned())
            .viewCount(notice.getViewCount())
            .publishedAt(notice.getPublishedAt())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .excerpt(excerpt)
            .imageUrl(imageUrl)
            .date(date)
            .attachments(attachmentResponses)
            .build();
    }

    /**
     * ProjectionDto -> Response DTO 변환
     */
    public static NoticeResponse from(NoticeProjectionDto dto) {
        String excerpt = generateExcerpt(dto.getContent());
        String imageUrl = extractFirstImageUrl(dto.getContent());
        String date = formatDate(dto.getPublishedAt());

        return NoticeResponse.builder()
            .id(dto.getId())
            .category(dto.getCategory())
            .title(dto.getTitle())
            .content(dto.getContent())
            .author(dto.getAuthor())
            .isPinned(dto.getIsPinned())
            .viewCount(dto.getViewCount())
            .publishedAt(dto.getPublishedAt())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .excerpt(excerpt)
            .imageUrl(imageUrl)
            .date(date)
            .attachments(null)
            .build();
    }

    /**
     * 날짜 포맷 변환 (YYYY.MM.DD)
     */
    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    /**
     * content에서 요약문 생성 (200자 제한)
     * HTML 태그 제거 후 순수 텍스트만 추출
     */
    private static String generateExcerpt(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        // HTML 태그 제거
        String plainText = content.replaceAll("<[^>]*>", " ")
                                  .replaceAll("&nbsp;", " ")
                                  .replaceAll("&amp;", "&")
                                  .replaceAll("&lt;", "<")
                                  .replaceAll("&gt;", ">")
                                  .replaceAll("&quot;", "\"")
                                  .trim();

        // 연속된 공백 제거
        plainText = plainText.replaceAll("\\s+", " ");

        // 200자 제한
        int maxLength = 200;
        if (plainText.length() <= maxLength) {
            return plainText;
        }

        // 단어 경계에서 자르기
        int lastSpaceIndex = plainText.lastIndexOf(' ', maxLength);
        if (lastSpaceIndex > 0) {
            return plainText.substring(0, lastSpaceIndex) + "...";
        }

        return plainText.substring(0, maxLength) + "...";
    }

    /**
     * content에서 첫 번째 이미지 URL 추출
     * <img src="..."> 태그에서 src 속성값 추출
     */
    private static String extractFirstImageUrl(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        // img 태그의 src 속성 추출
        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
