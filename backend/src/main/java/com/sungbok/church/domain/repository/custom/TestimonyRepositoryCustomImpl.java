package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.projection.TestimonyProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sungbok.church.domain.entity.QTestimony.testimony;

/**
 * Testimony Repository Custom Implementation
 * QueryDSL을 사용한 복잡한 쿼리 메서드 구현
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class TestimonyRepositoryCustomImpl implements TestimonyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TestimonyProjectionDto> searchByKeyword(String keyword, Pageable pageable) {
        // Content query
        List<TestimonyProjectionDto> content = queryFactory
            .select(Projections.constructor(TestimonyProjectionDto.class,
                testimony.id,
                testimony.title,
                testimony.author,
                testimony.content,
                testimony.category,
                testimony.isApproved,
                testimony.viewCount,
                testimony.publishedAt,
                testimony.createdAt,
                testimony.updatedAt
            ))
            .from(testimony)
            .where(
                testimony.isApproved.eq(true),
                testimony.title.containsIgnoreCase(keyword)
                    .or(testimony.content.containsIgnoreCase(keyword))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(testimony.publishedAt.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(testimony.count())
            .from(testimony)
            .where(
                testimony.isApproved.eq(true),
                testimony.title.containsIgnoreCase(keyword)
                    .or(testimony.content.containsIgnoreCase(keyword))
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
