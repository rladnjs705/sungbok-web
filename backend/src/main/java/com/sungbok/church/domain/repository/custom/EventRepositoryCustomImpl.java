package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.projection.EventProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.sungbok.church.domain.entity.QEvent.event;

/**
 * Event Repository QueryDSL Implementation
 *
 * Context7 Best Practices (QueryDSL 7.1):
 * - Projections.constructor() 사용으로 DTO 매핑
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - N+1 쿼리 방지 (단일 쿼리로 모든 필드 조회)
 * - BooleanExpression으로 동적 쿼리 조립
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventProjectionDto> findOngoingEvents(LocalDateTime currentDate) {
        return queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.startDate.loe(currentDate),  // startDate <= currentDate
                event.endDate.goe(currentDate)     // endDate >= currentDate
            )
            .orderBy(event.startDate.asc())
            .fetch();
    }

    @Override
    public List<EventProjectionDto> findAvailableEvents() {
        return queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.registrationRequired.eq(true),
                event.currentParticipants.lt(event.maxParticipants)  // currentParticipants < maxParticipants
            )
            .orderBy(event.startDate.asc())
            .fetch();
    }

    @Override
    public List<EventProjectionDto> findUpcomingEvents(LocalDate currentDate) {
        // LocalDateTime을 LocalDate로 캐스팅하여 비교
        // Expressions.dateTemplate()을 사용하여 CAST 표현
        return queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                // CAST(startDate AS date) >= currentDate
                Expressions.dateTemplate(LocalDate.class, "CAST({0} AS date)", event.startDate)
                    .goe(currentDate)
            )
            .orderBy(event.startDate.asc())
            .fetch();
    }

    @Override
    public Page<EventProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        BooleanExpression keywordCondition = event.title.containsIgnoreCase(keyword)
            .or(event.description.containsIgnoreCase(keyword))
            .or(event.location.containsIgnoreCase(keyword));

        List<EventProjectionDto> content = queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                keywordCondition
            )
            .orderBy(event.startDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(event.count())
            .from(event)
            .where(event.isPublished.eq(true), keywordCondition)
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<EventProjectionDto> findByCategoryAndPublished(String category, Pageable pageable) {
        List<EventProjectionDto> content = queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.category.eq(category)
            )
            .orderBy(event.startDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(event.count())
            .from(event)
            .where(event.isPublished.eq(true), event.category.eq(category))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<EventProjectionDto> findByDateRangeAndPublished(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    ) {
        List<EventProjectionDto> content = queryFactory
            .select(Projections.constructor(EventProjectionDto.class,
                event.id,
                event.title,
                event.description,
                event.category,
                event.organizer,
                event.posterImageUrl,
                event.location,
                event.startDate,
                event.endDate,
                event.registrationRequired,
                event.maxParticipants,
                event.currentParticipants,
                event.viewCount,
                event.isPublished,
                event.createdAt,
                event.updatedAt
            ))
            .from(event)
            .where(
                event.isPublished.eq(true),
                event.startDate.between(startDate, endDate)
            )
            .orderBy(event.startDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(event.count())
            .from(event)
            .where(event.isPublished.eq(true), event.startDate.between(startDate, endDate))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
