package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.response.GalleryImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sungbok.church.domain.entity.QGalleryImage.galleryImage;
import static com.sungbok.church.domain.entity.QGallery.gallery;

/**
 * GalleryImageRepository QueryDSL Implementation
 *
 * Context7 Best Practices (QueryDSL 7.1):
 * - Projections.constructor() 사용으로 DTO 매핑
 * - LEFT JOIN 명시적 작성으로 LazyInitializationException 방지
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - N+1 쿼리 방지
 */
@Repository
@RequiredArgsConstructor
public class GalleryImageRepositoryCustomImpl implements GalleryImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GalleryImageResponse> findByGalleryId(Long galleryId, Pageable pageable) {
        List<GalleryImageResponse> content = queryFactory
            .select(Projections.constructor(GalleryImageResponse.class,
                galleryImage.id,
                gallery.id,
                galleryImage.imageUrl,
                galleryImage.thumbnailUrl,
                galleryImage.caption,
                galleryImage.fileSize,
                galleryImage.width,
                galleryImage.height,
                galleryImage.displayOrder,
                galleryImage.createdAt,
                galleryImage.updatedAt
            ))
            .from(galleryImage)
            .leftJoin(galleryImage.gallery, gallery)  // 명시적 LEFT JOIN
            .where(gallery.id.eq(galleryId))
            .orderBy(galleryImage.displayOrder.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(galleryImage.count())
            .from(galleryImage)
            .where(galleryImage.gallery.id.eq(galleryId))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<GalleryImageResponse> findLatestImages(int limit) {
        return queryFactory
            .select(Projections.constructor(GalleryImageResponse.class,
                galleryImage.id, gallery.id, galleryImage.imageUrl,
                galleryImage.thumbnailUrl, galleryImage.caption, galleryImage.fileSize,
                galleryImage.width, galleryImage.height, galleryImage.displayOrder,
                galleryImage.createdAt, galleryImage.updatedAt
            ))
            .from(galleryImage)
            .leftJoin(galleryImage.gallery, gallery)
            .orderBy(
                galleryImage.createdAt.desc(),
                galleryImage.id.desc()  // 2차 정렬 기준 추가 (동일 시간 처리)
            )
            .limit(limit)
            .fetch();
    }

    @Override
    public List<GalleryImageResponse> findPopularImages(int limit) {
        // Note: 현재 GalleryImage에 viewCount가 없다면 createdAt 기준으로 정렬
        // viewCount가 있다면: .orderBy(galleryImage.viewCount.desc())
        return queryFactory
            .select(Projections.constructor(GalleryImageResponse.class,
                galleryImage.id, gallery.id, galleryImage.imageUrl,
                galleryImage.thumbnailUrl, galleryImage.caption, galleryImage.fileSize,
                galleryImage.width, galleryImage.height, galleryImage.displayOrder,
                galleryImage.createdAt, galleryImage.updatedAt
            ))
            .from(galleryImage)
            .leftJoin(galleryImage.gallery, gallery)
            .orderBy(galleryImage.createdAt.desc())  // viewCount가 있다면 변경
            .limit(limit)
            .fetch();
    }
}
