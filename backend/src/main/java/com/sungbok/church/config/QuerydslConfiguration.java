package com.sungbok.church.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL Configuration
 * JPAQueryFactory를 싱글톤 빈으로 등록
 *
 * Context7 Best Practices:
 * - JPAQueryFactory를 싱글톤 빈으로 관리하여 재사용성 향상
 * - EntityManager는 프록시로 주입되어 Thread-safe
 */
@Configuration
@RequiredArgsConstructor
public class QuerydslConfiguration {

    private final EntityManager entityManager;

    /**
     * JPAQueryFactory 빈 등록
     *
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
