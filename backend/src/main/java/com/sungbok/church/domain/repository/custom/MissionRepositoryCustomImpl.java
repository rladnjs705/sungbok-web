package com.sungbok.church.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sungbok.church.dto.projection.MissionProjectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.sungbok.church.domain.entity.QMission.mission;

/**
 * Mission Repository Custom Implementation
 * QueryDSL을 사용한 복잡한 쿼리 메서드 구현
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@Repository
@RequiredArgsConstructor
public class MissionRepositoryCustomImpl implements MissionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MissionProjectionDto> findOngoingMissions(LocalDate currentDate, Pageable pageable) {
        // Content query
        List<MissionProjectionDto> content = queryFactory
            .select(Projections.constructor(MissionProjectionDto.class,
                mission.id,
                mission.title,
                mission.type,
                mission.country,
                mission.region,
                mission.description,
                mission.missionaryName,
                mission.startDate,
                mission.endDate,
                mission.supportAmount,
                mission.photoUrl,
                mission.isActive,
                mission.createdAt,
                mission.updatedAt
            ))
            .from(mission)
            .where(
                mission.isActive.eq(true),
                mission.startDate.loe(currentDate),
                mission.endDate.isNull()
                    .or(mission.endDate.goe(currentDate))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(mission.startDate.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(mission.count())
            .from(mission)
            .where(
                mission.isActive.eq(true),
                mission.startDate.loe(currentDate),
                mission.endDate.isNull()
                    .or(mission.endDate.goe(currentDate))
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<MissionProjectionDto> findCompletedMissions(LocalDate currentDate, Pageable pageable) {
        // Content query
        List<MissionProjectionDto> content = queryFactory
            .select(Projections.constructor(MissionProjectionDto.class,
                mission.id,
                mission.title,
                mission.type,
                mission.country,
                mission.region,
                mission.description,
                mission.missionaryName,
                mission.startDate,
                mission.endDate,
                mission.supportAmount,
                mission.photoUrl,
                mission.isActive,
                mission.createdAt,
                mission.updatedAt
            ))
            .from(mission)
            .where(
                mission.isActive.eq(true),
                mission.endDate.lt(currentDate)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(mission.endDate.desc())
            .fetch();

        // Count query
        Long total = queryFactory
            .select(mission.count())
            .from(mission)
            .where(
                mission.isActive.eq(true),
                mission.endDate.lt(currentDate)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Long calculateTotalSupportAmount() {
        List<Long> amounts = queryFactory
            .select(mission.supportAmount)
            .from(mission)
            .where(
                mission.isActive.eq(true),
                mission.supportAmount.isNotNull()
            )
            .fetch();

        return amounts.stream()
            .mapToLong(Long::longValue)
            .sum();
    }
}
