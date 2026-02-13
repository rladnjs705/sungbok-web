package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Mission;
import com.sungbok.church.domain.enums.MissionType;

import java.time.LocalDate;

/**
 * Mission 엔티티 테스트 데이터 생성 Fixture
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class MissionFixture {

    public static MissionBuilder builder() {
        return new MissionBuilder();
    }

    /**
     * 진행 중인 선교 (현재 날짜가 startDate와 endDate 사이)
     */
    public static Mission ongoing() {
        return builder()
            .title("진행 중인 선교")
            .startDate(LocalDate.now().minusMonths(1))
            .endDate(LocalDate.now().plusMonths(1))
            .isActive(true)
            .build();
    }

    /**
     * 종료된 선교
     */
    public static Mission ended() {
        return builder()
            .title("종료된 선교")
            .startDate(LocalDate.now().minusMonths(6))
            .endDate(LocalDate.now().minusMonths(1))
            .isActive(false)
            .build();
    }

    /**
     * 국내 선교
     */
    public static Mission domestic() {
        return builder()
            .title("국내 선교")
            .type(MissionType.DOMESTIC)
            .country("대한민국")
            .region("서울")
            .build();
    }

    /**
     * 해외 선교
     */
    public static Mission overseas() {
        return builder()
            .title("해외 선교")
            .type(MissionType.OVERSEAS)
            .country("필리핀")
            .region("마닐라")
            .build();
    }

    public static class MissionBuilder {
        private String title = "테스트 선교";
        private MissionType type = MissionType.OVERSEAS;
        private String country = "대한민국";
        private String region = "서울";
        private String description = "선교 설명입니다.";
        private String missionaryName = "김선교 선교사";
        private LocalDate startDate = LocalDate.now();
        private LocalDate endDate = LocalDate.now().plusYears(1);
        private Long supportAmount = 1000000L;
        private String photoUrl = "https://example.com/mission.jpg";
        private Boolean isActive = true;

        public MissionBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MissionBuilder type(MissionType type) {
            this.type = type;
            return this;
        }

        public MissionBuilder country(String country) {
            this.country = country;
            return this;
        }

        public MissionBuilder region(String region) {
            this.region = region;
            return this;
        }

        public MissionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MissionBuilder missionaryName(String missionaryName) {
            this.missionaryName = missionaryName;
            return this;
        }

        public MissionBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public MissionBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public MissionBuilder supportAmount(Long supportAmount) {
            this.supportAmount = supportAmount;
            return this;
        }

        public MissionBuilder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public MissionBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Mission build() {
            return Mission.builder()
                .title(title)
                .type(type)
                .country(country)
                .region(region)
                .description(description)
                .missionaryName(missionaryName)
                .startDate(startDate)
                .endDate(endDate)
                .supportAmount(supportAmount)
                .photoUrl(photoUrl)
                .isActive(isActive)
                .build();
        }
    }
}
