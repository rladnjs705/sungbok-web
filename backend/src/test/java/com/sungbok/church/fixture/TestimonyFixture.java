package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Testimony;

import java.time.LocalDateTime;

/**
 * Testimony 엔티티 테스트 데이터 생성 Fixture
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class TestimonyFixture {

    public static TestimonyBuilder builder() {
        return new TestimonyBuilder();
    }

    /**
     * 승인된 간증
     */
    public static Testimony approved() {
        return builder()
            .title("승인된 간증")
            .isApproved(true)
            .publishedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 승인 대기 중인 간증
     */
    public static Testimony pending() {
        return builder()
            .title("승인 대기 중인 간증")
            .isApproved(false)
            .publishedAt(null)
            .build();
    }

    /**
     * 신앙 간증
     */
    public static Testimony faith() {
        return builder()
            .title("신앙 간증")
            .category("신앙")
            .isApproved(true)
            .build();
    }

    public static class TestimonyBuilder {
        private String title = "테스트 간증";
        private String author = "간증자";
        private String content = "간증 내용입니다.";
        private String category = "일반";
        private Boolean isApproved = false;
        private Integer viewCount = 0;
        private LocalDateTime publishedAt = LocalDateTime.now();

        public TestimonyBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TestimonyBuilder author(String author) {
            this.author = author;
            return this;
        }

        public TestimonyBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TestimonyBuilder category(String category) {
            this.category = category;
            return this;
        }

        public TestimonyBuilder isApproved(Boolean isApproved) {
            this.isApproved = isApproved;
            return this;
        }

        public TestimonyBuilder viewCount(Integer viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public TestimonyBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public Testimony build() {
            return Testimony.builder()
                .title(title)
                .author(author)
                .content(content)
                .category(category)
                .isApproved(isApproved)
                .viewCount(viewCount)
                .publishedAt(publishedAt)
                .build();
        }
    }
}
