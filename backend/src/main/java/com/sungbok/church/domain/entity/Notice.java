package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.NoticeCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 공지사항 Entity
 */
@Entity
@Table(name = "notice", indexes = {
    @Index(name = "idx_notice_published", columnList = "publishedAt DESC"),
    @Index(name = "idx_notice_category", columnList = "category"),
    @Index(name = "idx_notice_pinned", columnList = "isPinned, publishedAt DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Notice extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoticeCategory category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    private LocalDateTime publishedAt;
}
