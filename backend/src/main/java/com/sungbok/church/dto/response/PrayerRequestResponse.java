package com.sungbok.church.dto.response;

import com.sungbok.church.domain.entity.PrayerRequest;
import com.sungbok.church.domain.enums.PrayerStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PrayerRequest 조회 Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerRequestResponse {

    private Long id;
    private String title;
    private String content;
    private String requester;
    private Boolean isAnonymous;
    private Boolean isApproved;
    private PrayerStatus status;
    private LocalDateTime answeredAt;
    private Integer prayerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static PrayerRequestResponse from(PrayerRequest prayerRequest) {
        return PrayerRequestResponse.builder()
            .id(prayerRequest.getId())
            .title(prayerRequest.getTitle())
            .content(prayerRequest.getContent())
            .requester(prayerRequest.getIsAnonymous() ? "익명" : prayerRequest.getRequester())
            .isAnonymous(prayerRequest.getIsAnonymous())
            .isApproved(prayerRequest.getIsApproved())
            .status(prayerRequest.getStatus())
            .answeredAt(prayerRequest.getAnsweredAt())
            .prayerCount(prayerRequest.getPrayerCount())
            .createdAt(prayerRequest.getCreatedAt())
            .updatedAt(prayerRequest.getUpdatedAt())
            .build();
    }
}
