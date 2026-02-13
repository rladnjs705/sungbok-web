package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.PrayerRequest;
import com.sungbok.church.domain.enums.PrayerStatus;

import java.time.LocalDateTime;

/**
 * PrayerRequest 엔티티 테스트 데이터 생성 Fixture
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class PrayerRequestFixture {

    public static PrayerRequestBuilder builder() {
        return new PrayerRequestBuilder();
    }

    /**
     * 승인된 기도요청
     */
    public static PrayerRequest approved() {
        return builder()
            .title("승인된 기도요청")
            .isApproved(true)
            .status(PrayerStatus.PENDING)
            .build();
    }

    /**
     * 승인 대기 중인 기도요청
     */
    public static PrayerRequest pending() {
        return builder()
            .title("승인 대기 중인 기도요청")
            .isApproved(false)
            .status(PrayerStatus.PENDING)
            .build();
    }

    /**
     * 응답된 기도요청
     */
    public static PrayerRequest answered() {
        return builder()
            .title("응답된 기도요청")
            .isApproved(true)
            .status(PrayerStatus.ANSWERED)
            .answeredAt(LocalDateTime.now())
            .build();
    }

    /**
     * 익명 기도요청
     */
    public static PrayerRequest anonymous() {
        return builder()
            .title("익명 기도요청")
            .requester("익명")
            .isAnonymous(true)
            .build();
    }

    public static class PrayerRequestBuilder {
        private String title = "테스트 기도요청";
        private String content = "기도 내용입니다.";
        private String requester = "요청자";
        private Boolean isAnonymous = false;
        private Boolean isApproved = false;
        private PrayerStatus status = PrayerStatus.PENDING;
        private LocalDateTime answeredAt = null;
        private Integer prayerCount = 0;

        public PrayerRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PrayerRequestBuilder content(String content) {
            this.content = content;
            return this;
        }

        public PrayerRequestBuilder requester(String requester) {
            this.requester = requester;
            return this;
        }

        public PrayerRequestBuilder isAnonymous(Boolean isAnonymous) {
            this.isAnonymous = isAnonymous;
            return this;
        }

        public PrayerRequestBuilder isApproved(Boolean isApproved) {
            this.isApproved = isApproved;
            return this;
        }

        public PrayerRequestBuilder status(PrayerStatus status) {
            this.status = status;
            return this;
        }

        public PrayerRequestBuilder answeredAt(LocalDateTime answeredAt) {
            this.answeredAt = answeredAt;
            return this;
        }

        public PrayerRequestBuilder prayerCount(Integer prayerCount) {
            this.prayerCount = prayerCount;
            return this;
        }

        public PrayerRequest build() {
            return PrayerRequest.builder()
                .title(title)
                .content(content)
                .requester(requester)
                .isAnonymous(isAnonymous)
                .isApproved(isApproved)
                .status(status)
                .answeredAt(answeredAt)
                .prayerCount(prayerCount)
                .build();
        }
    }
}
