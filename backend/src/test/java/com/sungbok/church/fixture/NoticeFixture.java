package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.enums.NoticeCategory;

import java.time.LocalDateTime;

/**
 * Notice 엔티티 테스트 데이터 생성 Fixture
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class NoticeFixture {

    public static NoticeBuilder builder() {
        return new NoticeBuilder();
    }

    /**
     * 고정된 공지사항
     */
    public static Notice pinned() {
        return builder()
            .title("고정된 공지사항")
            .isPinned(true)
            .build();
    }

    /**
     * 일반 공지사항
     */
    public static Notice general() {
        return builder()
            .title("일반 공지사항")
            .category(NoticeCategory.ANNOUNCEMENT)
            .isPinned(false)
            .build();
    }

    /**
     * 행사 공지
     */
    public static Notice event() {
        return builder()
            .title("행사 공지")
            .category(NoticeCategory.EVENT)
            .build();
    }

    public static class NoticeBuilder {
        private NoticeCategory category = NoticeCategory.ANNOUNCEMENT;
        private String title = "테스트 공지사항";
        private String content = "공지사항 내용입니다.";
        private String author = "관리자";
        private Boolean isPinned = false;
        private Integer viewCount = 0;
        private LocalDateTime publishedAt = LocalDateTime.now();

        public NoticeBuilder category(NoticeCategory category) {
            this.category = category;
            return this;
        }

        public NoticeBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NoticeBuilder content(String content) {
            this.content = content;
            return this;
        }

        public NoticeBuilder author(String author) {
            this.author = author;
            return this;
        }

        public NoticeBuilder isPinned(Boolean isPinned) {
            this.isPinned = isPinned;
            return this;
        }

        public NoticeBuilder viewCount(Integer viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public NoticeBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public Notice build() {
            return Notice.builder()
                .category(category)
                .title(title)
                .content(content)
                .author(author)
                .isPinned(isPinned)
                .viewCount(viewCount)
                .publishedAt(publishedAt)
                .build();
        }
    }
}
