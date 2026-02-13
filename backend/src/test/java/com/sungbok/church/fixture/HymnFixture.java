package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Hymn;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hymn 엔티티 테스트 데이터 생성 Fixture
 *
 * 사용법:
 * <pre>
 * {@code
 * // 기본값으로 Hymn 생성
 * Hymn hymn = HymnFixture.builder().build();
 *
 * // 커스텀 값으로 Hymn 생성
 * Hymn customHymn = HymnFixture.builder()
 *     .title("주 예수 보다 더 귀한 것은 없네")
 *     .hymnNumber(370)
 *     .artist("김동일 간사")
 *     .build();
 *
 * // 공개된 찬양
 * Hymn published = HymnFixture.published();
 *
 * // 미공개 찬양
 * Hymn unpublished = HymnFixture.unpublished();
 * }
 * </pre>
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class HymnFixture {

    private static final AtomicInteger HYMN_NUMBER_COUNTER = new AtomicInteger(1);

    public static HymnBuilder builder() {
        return new HymnBuilder();
    }

    /**
     * 공개된 찬양
     */
    public static Hymn published() {
        return builder()
            .title("공개된 찬양")
            .isPublished(true)
            .build();
    }

    /**
     * 미공개 찬양
     */
    public static Hymn unpublished() {
        return builder()
            .title("미공개 찬양")
            .isPublished(false)
            .build();
    }

    /**
     * 최근 연주된 찬양
     */
    public static Hymn recent() {
        return builder()
            .title("최근 연주된 찬양")
            .performanceDate(LocalDate.now())
            .performanceCount(5)
            .build();
    }

    public static class HymnBuilder {
        private Integer hymnNumber = HYMN_NUMBER_COUNTER.getAndIncrement();
        private String title = "테스트 찬양";
        private String composer = "작곡가";
        private String lyricist = "작사가";
        private String lyrics = "가사 내용입니다.";
        private String artist = "성복교회 찬양팀";
        private String youtubeVideoId = "test-video-id";
        private String videoUrl = "https://youtube.com/watch?v=test";
        private String youtubeUrl = "https://youtube.com/watch?v=test";
        private String sheetMusicUrl = "https://example.com/sheet.pdf";
        private String thumbnailUrl = "https://example.com/thumb.jpg";
        private LocalDate performanceDate = LocalDate.now();
        private Integer viewCount = 0;
        private Integer performanceCount = 1;
        private Boolean isPublished = true;

        public HymnBuilder hymnNumber(Integer hymnNumber) {
            this.hymnNumber = hymnNumber;
            return this;
        }

        public HymnBuilder title(String title) {
            this.title = title;
            return this;
        }

        public HymnBuilder composer(String composer) {
            this.composer = composer;
            return this;
        }

        public HymnBuilder lyricist(String lyricist) {
            this.lyricist = lyricist;
            return this;
        }

        public HymnBuilder lyrics(String lyrics) {
            this.lyrics = lyrics;
            return this;
        }

        public HymnBuilder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public HymnBuilder youtubeVideoId(String youtubeVideoId) {
            this.youtubeVideoId = youtubeVideoId;
            return this;
        }

        public HymnBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public HymnBuilder youtubeUrl(String youtubeUrl) {
            this.youtubeUrl = youtubeUrl;
            return this;
        }

        public HymnBuilder sheetMusicUrl(String sheetMusicUrl) {
            this.sheetMusicUrl = sheetMusicUrl;
            return this;
        }

        public HymnBuilder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public HymnBuilder performanceDate(LocalDate performanceDate) {
            this.performanceDate = performanceDate;
            return this;
        }

        public HymnBuilder viewCount(Integer viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public HymnBuilder performanceCount(Integer performanceCount) {
            this.performanceCount = performanceCount;
            return this;
        }

        public HymnBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public Hymn build() {
            return Hymn.builder()
                .hymnNumber(hymnNumber)
                .title(title)
                .composer(composer)
                .lyricist(lyricist)
                .lyrics(lyrics)
                .artist(artist)
                .youtubeVideoId(youtubeVideoId)
                .videoUrl(videoUrl)
                .youtubeUrl(youtubeUrl)
                .sheetMusicUrl(sheetMusicUrl)
                .thumbnailUrl(thumbnailUrl)
                .performanceDate(performanceDate)
                .viewCount(viewCount)
                .performanceCount(performanceCount)
                .isPublished(isPublished)
                .build();
        }
    }
}
