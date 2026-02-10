package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.dto.projection.SermonProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.sungbok.church.domain.entity.QSermon.sermon;
import static com.sungbok.church.domain.entity.QWorship.worship;

/**
 * SermonRepository QueryDSL Implementation
 *
 * Context7 Best Practices (QueryDSL 7.1):
 * - Projections.constructor() 사용으로 DTO 매핑
 * - LEFT JOIN 명시적 작성으로 LazyInitializationException 방지
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - N+1 쿼리 방지
 */
@Repository
@RequiredArgsConstructor
public class SermonRepositoryCustomImpl implements SermonRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SermonProjectionDto> findPublishedSermons(Pageable pageable) {
        List<SermonProjectionDto> content = queryFactory
            .select(Projections.constructor(SermonProjectionDto.class,
                sermon.id,
                sermon.title,
                sermon.preacher,
                sermon.sermonDate,
                sermon.bibleVerse,
                sermon.summary,
                sermon.youtubeVideoId,
                sermon.videoUrl,
                sermon.thumbnailUrl,
                sermon.duration,
                sermon.tags,
                sermon.description,
                sermon.isFeatured,
                sermon.isPublished,
                sermon.viewCount,
                sermon.createdAt,
                sermon.updatedAt,
                worship.id,
                worship.title,
                worship.type.stringValue()  // Enum → String 변환
            ))
            .from(sermon)
            .leftJoin(sermon.worship, worship)  // 명시적 LEFT JOIN
            .where(sermon.isPublished.eq(true))
            .orderBy(sermon.sermonDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(sermon.count())
            .from(sermon)
            .where(sermon.isPublished.eq(true))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<SermonProjectionDto> findByPreacher(String preacher, Pageable pageable) {
        List<SermonProjectionDto> content = queryFactory
            .select(Projections.constructor(SermonProjectionDto.class,
                sermon.id, sermon.title, sermon.preacher, sermon.sermonDate,
                sermon.bibleVerse, sermon.summary, sermon.youtubeVideoId, sermon.videoUrl,
                sermon.thumbnailUrl, sermon.duration, sermon.tags, sermon.description,
                sermon.isFeatured, sermon.isPublished, sermon.viewCount,
                sermon.createdAt, sermon.updatedAt,
                worship.id, worship.title, worship.type.stringValue()
            ))
            .from(sermon)
            .leftJoin(sermon.worship, worship)
            .where(
                sermon.preacher.eq(preacher),
                sermon.isPublished.eq(true)
            )
            .orderBy(sermon.sermonDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(sermon.count())
            .from(sermon)
            .where(sermon.preacher.eq(preacher), sermon.isPublished.eq(true))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<SermonProjectionDto> findByDateRange(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        List<SermonProjectionDto> content = queryFactory
            .select(Projections.constructor(SermonProjectionDto.class,
                sermon.id, sermon.title, sermon.preacher, sermon.sermonDate,
                sermon.bibleVerse, sermon.summary, sermon.youtubeVideoId, sermon.videoUrl,
                sermon.thumbnailUrl, sermon.duration, sermon.tags, sermon.description,
                sermon.isFeatured, sermon.isPublished, sermon.viewCount,
                sermon.createdAt, sermon.updatedAt,
                worship.id, worship.title, worship.type.stringValue()
            ))
            .from(sermon)
            .leftJoin(sermon.worship, worship)
            .where(
                sermon.sermonDate.between(startDate, endDate),
                sermon.isPublished.eq(true)
            )
            .orderBy(sermon.sermonDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(sermon.count())
            .from(sermon)
            .where(sermon.sermonDate.between(startDate, endDate), sermon.isPublished.eq(true))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<SermonProjectionDto> findFeatured() {
        return queryFactory
            .select(Projections.constructor(SermonProjectionDto.class,
                sermon.id, sermon.title, sermon.preacher, sermon.sermonDate,
                sermon.bibleVerse, sermon.summary, sermon.youtubeVideoId, sermon.videoUrl,
                sermon.thumbnailUrl, sermon.duration, sermon.tags, sermon.description,
                sermon.isFeatured, sermon.isPublished, sermon.viewCount,
                sermon.createdAt, sermon.updatedAt,
                worship.id, worship.title, worship.type.stringValue()
            ))
            .from(sermon)
            .leftJoin(sermon.worship, worship)
            .where(
                sermon.isFeatured.eq(true),
                sermon.isPublished.eq(true)
            )
            .orderBy(sermon.sermonDate.desc())
            .fetch();
    }

    @Override
    public List<SermonProjectionDto> findLatest() {
        return queryFactory
            .select(Projections.constructor(SermonProjectionDto.class,
                sermon.id, sermon.title, sermon.preacher, sermon.sermonDate,
                sermon.bibleVerse, sermon.summary, sermon.youtubeVideoId, sermon.videoUrl,
                sermon.thumbnailUrl, sermon.duration, sermon.tags, sermon.description,
                sermon.isFeatured, sermon.isPublished, sermon.viewCount,
                sermon.createdAt, sermon.updatedAt,
                worship.id, worship.title, worship.type.stringValue()
            ))
            .from(sermon)
            .leftJoin(sermon.worship, worship)
            .where(sermon.isPublished.eq(true))
            .orderBy(sermon.sermonDate.desc())
            .limit(10)
            .fetch();
    }

    @Override
    public List<SermonProjectionDto> findByWorshipType(WorshipType worshipType, int limit) {
        return queryFactory
            .select(Projections.constructor(SermonProjectionDto.class,
                sermon.id, sermon.title, sermon.preacher, sermon.sermonDate,
                sermon.bibleVerse, sermon.summary, sermon.youtubeVideoId, sermon.videoUrl,
                sermon.thumbnailUrl, sermon.duration, sermon.tags, sermon.description,
                sermon.isFeatured, sermon.isPublished, sermon.viewCount,
                sermon.createdAt, sermon.updatedAt,
                worship.id, worship.title, worship.type.stringValue()
            ))
            .from(sermon)
            .leftJoin(sermon.worship, worship)
            .where(
                worship.type.eq(worshipType),
                sermon.isPublished.eq(true)
            )
            .orderBy(sermon.sermonDate.desc())
            .limit(limit)
            .fetch();
    }
}
