package com.sungbok.church.fixture;

import com.sungbok.church.domain.entity.Bulletin;

import java.time.LocalDate;

/**
 * Bulletin 엔티티 테스트 데이터 생성 Fixture
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
public class BulletinFixture {

    public static BulletinBuilder builder() {
        return new BulletinBuilder();
    }

    /**
     * 공개된 주보
     */
    public static Bulletin published() {
        return builder()
            .title("공개된 주보")
            .isPublished(true)
            .build();
    }

    /**
     * 미공개 주보
     */
    public static Bulletin unpublished() {
        return builder()
            .title("미공개 주보")
            .isPublished(false)
            .build();
    }

    /**
     * 최근 주보
     */
    public static Bulletin recent() {
        return builder()
            .title("최근 주보")
            .bulletinDate(LocalDate.now())
            .build();
    }

    public static class BulletinBuilder {
        private String title = "테스트 주보";
        private LocalDate bulletinDate = LocalDate.now();
        private String pdfUrl = "https://example.com/bulletin.pdf";
        private Long fileSize = 1024000L; // 1MB
        private String thumbnailUrl = "https://example.com/bulletin-thumb.jpg";
        private Integer downloadCount = 0;
        private Boolean isPublished = true;

        public BulletinBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BulletinBuilder bulletinDate(LocalDate bulletinDate) {
            this.bulletinDate = bulletinDate;
            return this;
        }

        public BulletinBuilder pdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }

        public BulletinBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public BulletinBuilder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public BulletinBuilder downloadCount(Integer downloadCount) {
            this.downloadCount = downloadCount;
            return this;
        }

        public BulletinBuilder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        public Bulletin build() {
            return Bulletin.builder()
                .title(title)
                .bulletinDate(bulletinDate)
                .pdfUrl(pdfUrl)
                .fileSize(fileSize)
                .thumbnailUrl(thumbnailUrl)
                .downloadCount(downloadCount)
                .isPublished(isPublished)
                .build();
        }
    }
}
