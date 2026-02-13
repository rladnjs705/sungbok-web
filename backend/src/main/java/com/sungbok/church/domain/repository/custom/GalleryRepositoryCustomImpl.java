package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.response.GalleryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.sungbok.church.domain.entity.QGallery.gallery;

/**
 * GalleryRepository QueryDSL Implementation
 *
 * Context7 Best Practices (QueryDSL 7.1):
 * - Projections.constructor() 사용으로 DTO 매핑
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - N+1 쿼리 방지
 * - LazyInitializationException 방지
 */
@Repository
@RequiredArgsConstructor
public class GalleryRepositoryCustomImpl implements GalleryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GalleryResponse> findPublishedGalleriesWithProjection(Pageable pageable) {
        List<GalleryResponse> content = queryFactory
            .select(Projections.constructor(GalleryResponse.class,
                gallery.id,
                gallery.title,
                gallery.description,
                gallery.eventDate,
                gallery.coverImageUrl,
                gallery.viewCount,
                gallery.isPublished,
                gallery.createdAt,
                gallery.updatedAt
            ))
            .from(gallery)
            .where(gallery.isPublished.eq(true))
            .orderBy(gallery.eventDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(gallery.count())
            .from(gallery)
            .where(gallery.isPublished.eq(true))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<GalleryResponse> findByDateRangeWithProjection(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        List<GalleryResponse> content = queryFactory
            .select(Projections.constructor(GalleryResponse.class,
                gallery.id,
                gallery.title,
                gallery.description,
                gallery.eventDate,
                gallery.coverImageUrl,
                gallery.viewCount,
                gallery.isPublished,
                gallery.createdAt,
                gallery.updatedAt
            ))
            .from(gallery)
            .where(
                gallery.eventDate.between(startDate, endDate),
                gallery.isPublished.eq(true)
            )
            .orderBy(gallery.eventDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(gallery.count())
            .from(gallery)
            .where(
                gallery.eventDate.between(startDate, endDate),
                gallery.isPublished.eq(true)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<GalleryResponse> searchByTitleWithProjection(String keyword, Pageable pageable) {
        List<GalleryResponse> content = queryFactory
            .select(Projections.constructor(GalleryResponse.class,
                gallery.id,
                gallery.title,
                gallery.description,
                gallery.eventDate,
                gallery.coverImageUrl,
                gallery.viewCount,
                gallery.isPublished,
                gallery.createdAt,
                gallery.updatedAt
            ))
            .from(gallery)
            .where(
                gallery.title.contains(keyword),
                gallery.isPublished.eq(true)
            )
            .orderBy(gallery.eventDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(gallery.count())
            .from(gallery)
            .where(
                gallery.title.contains(keyword),
                gallery.isPublished.eq(true)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<GalleryResponse> searchByKeywordWithProjection(String keyword, Pageable pageable) {
        List<GalleryResponse> content = queryFactory
            .select(Projections.constructor(GalleryResponse.class,
                gallery.id,
                gallery.title,
                gallery.description,
                gallery.eventDate,
                gallery.coverImageUrl,
                gallery.viewCount,
                gallery.isPublished,
                gallery.createdAt,
                gallery.updatedAt
            ))
            .from(gallery)
            .where(
                gallery.title.contains(keyword)
                    .or(gallery.description.contains(keyword)),
                gallery.isPublished.eq(true)
            )
            .orderBy(gallery.eventDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(gallery.count())
            .from(gallery)
            .where(
                gallery.title.contains(keyword)
                    .or(gallery.description.contains(keyword)),
                gallery.isPublished.eq(true)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<GalleryResponse> findLatestWithProjection(int limit) {
        return queryFactory
            .select(Projections.constructor(GalleryResponse.class,
                gallery.id,
                gallery.title,
                gallery.description,
                gallery.eventDate,
                gallery.coverImageUrl,
                gallery.viewCount,
                gallery.isPublished,
                gallery.createdAt,
                gallery.updatedAt
            ))
            .from(gallery)
            .where(gallery.isPublished.eq(true))
            .orderBy(gallery.eventDate.desc())
            .limit(limit)
            .fetch();
    }
}
