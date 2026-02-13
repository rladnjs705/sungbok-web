package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.projection.NoticeProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sungbok.church.domain.entity.QNotice.notice;

/**
 * Notice Repository Custom Implementation
 * QueryDSL을 사용한 복잡한 쿼리 메서드 구현
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        // Content query
        List<NoticeProjectionDto> content = queryFactory
            .select(Projections.constructor(NoticeProjectionDto.class,
                notice.id,
                notice.category,
                notice.title,
                notice.content,
                notice.author,
                notice.isPinned,
                notice.viewCount,
                notice.publishedAt,
                notice.createdAt,
                notice.updatedAt
            ))
            .from(notice)
            .where(
                notice.title.containsIgnoreCase(keyword)
                    .or(notice.content.containsIgnoreCase(keyword))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(notice.publishedAt.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(notice.count())
            .from(notice)
            .where(
                notice.title.containsIgnoreCase(keyword)
                    .or(notice.content.containsIgnoreCase(keyword))
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
