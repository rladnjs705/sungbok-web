package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 정적 페이지 Entity (인사말, 교회 연혁, 오시는 길 등)
 */
@Entity
@Table(name = "page", indexes = {
    @Index(name = "idx_page_slug", columnList = "slug", unique = true),
    @Index(name = "idx_page_published", columnList = "isPublished, displayOrder")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Page extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 300)
    private String metaDescription;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;

    private Integer displayOrder;
}
