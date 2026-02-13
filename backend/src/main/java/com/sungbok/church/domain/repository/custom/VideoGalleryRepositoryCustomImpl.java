package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.projection.VideoGalleryProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sungbok.church.domain.entity.QVideoGallery.videoGallery;

/**
 * VideoGallery Repository Custom Implementation
 * QueryDSL을 사용한 복잡한 쿼리 메서드 구현
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class VideoGalleryRepositoryCustomImpl implements VideoGalleryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<VideoGalleryProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        // Content query
        List<VideoGalleryProjectionDto> content = queryFactory
            .select(Projections.constructor(VideoGalleryProjectionDto.class,
                videoGallery.id,
                videoGallery.title,
                videoGallery.description,
                videoGallery.youtubeVideoId,
                videoGallery.videoUrl,
                videoGallery.thumbnailUrl,
                videoGallery.eventDate,
                videoGallery.category,
                videoGallery.viewCount,
                videoGallery.isPublished,
                videoGallery.createdAt,
                videoGallery.updatedAt
            ))
            .from(videoGallery)
            .where(
                videoGallery.isPublished.eq(true),
                videoGallery.title.containsIgnoreCase(keyword)
                    .or(videoGallery.description.containsIgnoreCase(keyword))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(videoGallery.eventDate.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(videoGallery.count())
            .from(videoGallery)
            .where(
                videoGallery.isPublished.eq(true),
                videoGallery.title.containsIgnoreCase(keyword)
                    .or(videoGallery.description.containsIgnoreCase(keyword))
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
