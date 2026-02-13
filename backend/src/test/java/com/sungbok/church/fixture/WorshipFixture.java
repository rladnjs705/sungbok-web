package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Worship 엔티티 테스트 데이터 생성 Fixture
 *
 * 사용법:
 * <pre>
 * {@code
 * // 주일예배
 * Worship sunday = WorshipFixture.sunday();
 *
 * // 수요예배
 * Worship wednesday = WorshipFixture.wednesday();
 *
 * // 커스텀 예배
 * Worship custom = WorshipFixture.builder()
 *     .title("새벽예배")
 *     .type(WorshipType.DAWN)
 *     .dayOfWeek(DayOfWeek.TUESDAY)
 *     .startTime(LocalTime.of(5, 30))
 *     .build();
 * }
 * </pre>
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class WorshipFixture {

    public static WorshipBuilder builder() {
        return new WorshipBuilder();
    }

    /**
     * 주일예배
     */
    public static Worship sunday() {
        return builder()
            .title("주일예배")
            .type(WorshipType.SUNDAY)
            .dayOfWeek(DayOfWeek.SUNDAY)
            .startTime(LocalTime.of(10, 30))
            .build();
    }

    /**
     * 수요예배
     */
    public static Worship wednesday() {
        return builder()
            .title("수요예배")
            .type(WorshipType.WEDNESDAY)
            .dayOfWeek(DayOfWeek.WEDNESDAY)
            .startTime(LocalTime.of(19, 30))
            .build();
    }

    /**
     * 새벽예배
     */
    public static Worship dawn() {
        return builder()
            .title("새벽예배")
            .type(WorshipType.DAWN)
            .dayOfWeek(DayOfWeek.TUESDAY)
            .startTime(LocalTime.of(5, 30))
            .build();
    }

    /**
     * 특별예배
     */
    public static Worship special() {
        return builder()
            .title("특별예배")
            .type(WorshipType.SPECIAL)
            .dayOfWeek(DayOfWeek.SUNDAY)
            .startTime(LocalTime.of(19, 30))
            .build();
    }

    public static class WorshipBuilder {
        private WorshipType type = WorshipType.SUNDAY;
        private String title = "테스트 예배";
        private String description = "테스트 예배 설명";
        private DayOfWeek dayOfWeek = DayOfWeek.SUNDAY;
        private LocalTime startTime = LocalTime.of(10, 30);
        private String location = "본당";
        private String liveStreamUrl = "https://youtube.com/live";
        private Boolean isLiveNow = false;
        private Boolean isActive = true;

        public WorshipBuilder type(WorshipType type) {
            this.type = type;
            return this;
        }

        public WorshipBuilder title(String title) {
            this.title = title;
            return this;
        }

        public WorshipBuilder description(String description) {
            this.description = description;
            return this;
        }

        public WorshipBuilder dayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public WorshipBuilder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public WorshipBuilder location(String location) {
            this.location = location;
            return this;
        }

        public WorshipBuilder liveStreamUrl(String liveStreamUrl) {
            this.liveStreamUrl = liveStreamUrl;
            return this;
        }

        public WorshipBuilder isLiveNow(Boolean isLiveNow) {
            this.isLiveNow = isLiveNow;
            return this;
        }

        public WorshipBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Worship build() {
            return Worship.builder()
                .type(type)
                .title(title)
                .description(description)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .location(location)
                .liveStreamUrl(liveStreamUrl)
                .isLiveNow(isLiveNow)
                .isActive(isActive)
                .build();
        }
    }
}
