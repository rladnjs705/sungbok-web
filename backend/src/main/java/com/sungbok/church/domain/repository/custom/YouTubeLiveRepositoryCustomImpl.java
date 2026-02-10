package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.dto.response.YouTubeLiveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.sungbok.church.domain.entity.QYouTubeLive.youTubeLive;
import static com.sungbok.church.domain.entity.QWorship.worship;

/**
 * YouTubeLiveRepository QueryDSL Implementation
 *
 * Context7 Best Practices (QueryDSL 7.1):
 * - Projections.constructor() 사용으로 DTO 매핑
 * - LEFT JOIN 명시적 작성으로 LazyInitializationException 방지
 * - 타입 안전성 확보 (컴파일 타임 검증)
 * - N+1 쿼리 방지
 */
@Repository
@RequiredArgsConstructor
public class YouTubeLiveRepositoryCustomImpl implements YouTubeLiveRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<YouTubeLiveResponse> findAllWithDetails(Pageable pageable) {
        List<YouTubeLiveResponse> content = queryFactory
            .select(Projections.constructor(YouTubeLiveResponse.class,
                youTubeLive.id,
                worship.id,
                worship.title,
                youTubeLive.youtubeVideoId,
                youTubeLive.title,
                youTubeLive.scheduledStartTime,
                youTubeLive.actualStartTime,
                youTubeLive.endTime,
                youTubeLive.status,  // Enum 직접 전달
                youTubeLive.viewerCount,
                youTubeLive.createdAt,
                youTubeLive.updatedAt
            ))
            .from(youTubeLive)
            .leftJoin(youTubeLive.worship, worship)  // 명시적 LEFT JOIN
            .orderBy(youTubeLive.scheduledStartTime.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(youTubeLive.count())
            .from(youTubeLive)
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<YouTubeLiveResponse> findByWorshipType(WorshipType worshipType) {
        return queryFactory
            .select(Projections.constructor(YouTubeLiveResponse.class,
                youTubeLive.id, worship.id, worship.title, youTubeLive.youtubeVideoId,
                youTubeLive.title, youTubeLive.scheduledStartTime, youTubeLive.actualStartTime,
                youTubeLive.endTime, youTubeLive.status, youTubeLive.viewerCount,
                youTubeLive.createdAt, youTubeLive.updatedAt
            ))
            .from(youTubeLive)
            .leftJoin(youTubeLive.worship, worship)
            .where(worship.type.eq(worshipType))
            .orderBy(youTubeLive.scheduledStartTime.desc())
            .fetch();
    }

    @Override
    public List<YouTubeLiveResponse> findLatest(int limit) {
        return queryFactory
            .select(Projections.constructor(YouTubeLiveResponse.class,
                youTubeLive.id, worship.id, worship.title, youTubeLive.youtubeVideoId,
                youTubeLive.title, youTubeLive.scheduledStartTime, youTubeLive.actualStartTime,
                youTubeLive.endTime, youTubeLive.status, youTubeLive.viewerCount,
                youTubeLive.createdAt, youTubeLive.updatedAt
            ))
            .from(youTubeLive)
            .leftJoin(youTubeLive.worship, worship)
            .orderBy(youTubeLive.scheduledStartTime.desc())
            .limit(limit)
            .fetch();
    }

    @Override
    public Page<YouTubeLiveResponse> findUpcoming(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        List<YouTubeLiveResponse> content = queryFactory
            .select(Projections.constructor(YouTubeLiveResponse.class,
                youTubeLive.id, worship.id, worship.title, youTubeLive.youtubeVideoId,
                youTubeLive.title, youTubeLive.scheduledStartTime, youTubeLive.actualStartTime,
                youTubeLive.endTime, youTubeLive.status, youTubeLive.viewerCount,
                youTubeLive.createdAt, youTubeLive.updatedAt
            ))
            .from(youTubeLive)
            .leftJoin(youTubeLive.worship, worship)
            .where(youTubeLive.scheduledStartTime.after(now))
            .orderBy(youTubeLive.scheduledStartTime.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(youTubeLive.count())
            .from(youTubeLive)
            .where(youTubeLive.scheduledStartTime.after(now))
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
