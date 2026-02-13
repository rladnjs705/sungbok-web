package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Event;

import java.time.LocalDateTime;

/**
 * Event 엔티티 테스트 데이터 생성 Fixture
 *
 * 목적:
 * - 재사용 가능한 테스트 데이터 생성
 * - 테스트 코드 간소화 및 가독성 향상
 * - 엔티티 필드 기본값 관리
 *
 * 사용법:
 * <pre>
 * {@code
 * // 기본값으로 Event 생성
 * Event event = EventFixture.builder().build();
 *
 * // 커스텀 값으로 Event 생성
 * Event customEvent = EventFixture.builder()
 *     .title("특별 집회")
 *     .startDate(LocalDateTime.now().plusDays(7))
 *     .maxParticipants(200)
 *     .build();
 *
 * // 진행 중인 행사
 * Event ongoingEvent = EventFixture.ongoing();
 *
 * // 예정된 행사
 * Event upcomingEvent = EventFixture.upcoming();
 *
 * // 종료된 행사
 * Event endedEvent = EventFixture.ended();
 * }
 * </pre>
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class EventFixture {

    /**
     * EventBuilder 인스턴스 반환
     *
     * @return EventBuilder
     */
    public static EventBuilder builder() {
        return new EventBuilder();
    }

    /**
     * 진행 중인 행사 (현재 시간이 startDate와 endDate 사이)
     *
     * @return Event 엔티티
     */
    public static Event ongoing() {
        return builder()
            .title("진행 중인 행사")
            .startDate(LocalDateTime.now().minusDays(1))
            .endDate(LocalDateTime.now().plusDays(1))
            .build();
    }

    /**
     * 예정된 행사 (startDate가 미래)
     *
     * @return Event 엔티티
     */
    public static Event upcoming() {
        return builder()
            .title("예정된 행사")
            .startDate(LocalDateTime.now().plusDays(7))
            .endDate(LocalDateTime.now().plusDays(8))
            .build();
    }

    /**
     * 종료된 행사 (endDate가 과거)
     *
     * @return Event 엔티티
     */
    public static Event ended() {
        return builder()
            .title("종료된 행사")
            .startDate(LocalDateTime.now().minusDays(8))
            .endDate(LocalDateTime.now().minusDays(7))
            .build();
    }

    /**
     * 참가 가능한 행사 (정원 미달)
     *
     * @return Event 엔티티
     */
    public static Event available() {
        return builder()
            .title("참가 가능한 행사")
            .registrationRequired(true)
            .currentParticipants(10)
            .maxParticipants(100)
            .build();
    }

    /**
     * 정원 마감 행사
     *
     * @return Event 엔티티
     */
    public static Event full() {
        return builder()
            .title("정원 마감 행사")
            .registrationRequired(true)
            .currentParticipants(100)
            .maxParticipants(100)
            .build();
    }

    /**
     * 공개된 행사
     *
     * @return Event 엔티티
     */
    public static Event published() {
        return builder()
            .title("공개된 행사")
            .isPublished(true)
            .build();
    }

    /**
     * 미공개 행사
     *
     * @return Event 엔티티
     */
    public static Event unpublished() {
        return builder()
            .title("미공개 행사")
            .isPublished(false)
            .build();
    }

    /**
     * Event Builder 클래스
     */
    public static class EventBuilder {
        private String title = "테스트 행사";
        private String description = "테스트 행사 설명입니다.";
        private String category = "일반";
        private String organizer = "성복교회";
        private String posterImageUrl = "https://example.com/poster.jpg";
        private String location = "본당";
        private LocalDateTime startDate = LocalDateTime.now();
        private LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        private Boolean registrationRequired = false;
        private Integer maxParticipants = 100;
        private Integer currentParticipants = 0;
        private Integer viewCount = 0;
        private Boolean isPublished = true;

        public EventBuilder title(String title) {
            this.title = title;
            return this;
        }

        public EventBuilder description(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder category(String category) {
            this.category = category;
            return this;
        }

        public EventBuilder organizer(String organizer) {
            this.organizer = organizer;
            return this;
        }

        public EventBuilder posterImageUrl(String posterImageUrl) {
            this.posterImageUrl = posterImageUrl;
            return this;
        }

        public EventBuilder location(String location) {
            this.location = location;
            return this;
        }

        public EventBuilder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public EventBuilder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public EventBuilder registrationRequired(Boolean registrationRequired) {
            this.registrationRequired = registrationRequired;
            return this;
        }

        public EventBuilder maxParticipants(Integer maxParticipants) {
            this.maxParticipants = maxParticipants;
            return this;
        }

        public EventBuilder currentParticipants(Integer currentParticipants) {
            this.currentParticipants = currentParticipants;
            return this;
        }

        public EventBuilder viewCount(Integer viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public EventBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        /**
         * Event 엔티티 빌드
         *
         * @return Event 엔티티
         */
        public Event build() {
            return Event.builder()
                .title(title)
                .description(description)
                .category(category)
                .organizer(organizer)
                .posterImageUrl(posterImageUrl)
                .location(location)
                .startDate(startDate)
                .endDate(endDate)
                .registrationRequired(registrationRequired)
                .maxParticipants(maxParticipants)
                .currentParticipants(currentParticipants)
                .viewCount(viewCount)
                .isPublished(isPublished)
                .build();
        }
    }
}
