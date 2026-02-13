package com.sungbok.church.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sungbok.church.domain.entity.QPrayerRequest.prayerRequest;

/**
 * PrayerRequest Repository Custom Implementation
 * QueryDSL을 사용한 복잡한 쿼리 메서드 구현
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class PrayerRequestRepositoryCustomImpl implements PrayerRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long incrementPrayerCount(Long id) {
        return queryFactory
            .update(prayerRequest)
            .set(prayerRequest.prayerCount, prayerRequest.prayerCount.add(1))
            .where(prayerRequest.id.eq(id))
            .execute();
    }
}
