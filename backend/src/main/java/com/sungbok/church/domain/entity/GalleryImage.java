package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 갤러리 이미지 Entity
 */
@Entity
@Table(name = "gallery_image", indexes = {
    @Index(name = "idx_image_gallery", columnList = "gallery_id, displayOrder")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"gallery"})
public class GalleryImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id", nullable = false)
    private Gallery gallery;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(length = 500)
    private String caption;

    @Column(nullable = false)
    private Long fileSize;

    private Integer width;

    private Integer height;

    private Integer displayOrder;
}
