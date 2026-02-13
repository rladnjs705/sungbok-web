package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.projection.HymnProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sungbok.church.domain.entity.QHymn.hymn;

/**
 * Hymn Repository Custom Implementation
 * QueryDSL을 사용한 복잡한 쿼리 메서드 구현
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class HymnRepositoryCustomImpl implements HymnRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HymnProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        // Content query
        List<HymnProjectionDto> content = queryFactory
            .select(Projections.constructor(HymnProjectionDto.class,
                hymn.id,
                hymn.hymnNumber,
                hymn.title,
                hymn.composer,
                hymn.lyricist,
                hymn.lyrics,
                hymn.artist,
                hymn.youtubeVideoId,
                hymn.videoUrl,
                hymn.youtubeUrl,
                hymn.sheetMusicUrl,
                hymn.thumbnailUrl,
                hymn.performanceDate,
                hymn.viewCount,
                hymn.performanceCount,
                hymn.isPublished,
                hymn.createdAt,
                hymn.updatedAt
            ))
            .from(hymn)
            .where(
                hymn.isPublished.eq(true),
                hymn.title.containsIgnoreCase(keyword)
                    .or(hymn.lyrics.containsIgnoreCase(keyword))
                    .or(hymn.artist.containsIgnoreCase(keyword))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(hymn.performanceDate.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(hymn.count())
            .from(hymn)
            .where(
                hymn.isPublished.eq(true),
                hymn.title.containsIgnoreCase(keyword)
                    .or(hymn.lyrics.containsIgnoreCase(keyword))
                    .or(hymn.artist.containsIgnoreCase(keyword))
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<HymnProjectionDto> findByArtistWithStats(String artist, Pageable pageable) {
        // Content query
        List<HymnProjectionDto> content = queryFactory
            .select(Projections.constructor(HymnProjectionDto.class,
                hymn.id,
                hymn.hymnNumber,
                hymn.title,
                hymn.composer,
                hymn.lyricist,
                hymn.lyrics,
                hymn.artist,
                hymn.youtubeVideoId,
                hymn.videoUrl,
                hymn.youtubeUrl,
                hymn.sheetMusicUrl,
                hymn.thumbnailUrl,
                hymn.performanceDate,
                hymn.viewCount,
                hymn.performanceCount,
                hymn.isPublished,
                hymn.createdAt,
                hymn.updatedAt
            ))
            .from(hymn)
            .where(
                hymn.isPublished.eq(true),
                hymn.artist.containsIgnoreCase(artist)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(hymn.performanceCount.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(hymn.count())
            .from(hymn)
            .where(
                hymn.isPublished.eq(true),
                hymn.artist.containsIgnoreCase(artist)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Long calculateTotalPerformanceCount() {
        // QueryDSL 7.1에서는 sum()을 직접 지원하지 않으므로 stream 사용
        List<Integer> counts = queryFactory
            .select(hymn.performanceCount)
            .from(hymn)
            .where(
                hymn.isPublished.eq(true),
                hymn.performanceCount.isNotNull()
            )
            .fetch();

        return counts.stream()
            .mapToLong(Integer::longValue)
            .sum();
    }
}
