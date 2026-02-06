package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 간증 Entity
 */
@Entity
@Table(name = "testimony", indexes = {
    @Index(name = "idx_testimony_published", columnList = "publishedAt DESC"),
    @Index(name = "idx_testimony_approved", columnList = "isApproved, publishedAt DESC"),
    @Index(name = "idx_testimony_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Testimony extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isApproved = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    private LocalDateTime publishedAt;
}
