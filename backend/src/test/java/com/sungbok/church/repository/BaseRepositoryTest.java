package com.sungbok.church.repository;

import com.sungbok.church.config.QuerydslConfiguration;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository 테스트 기본 클래스
 *
 * 목적:
 * - @SpringBootTest + @Transactional로 통합 테스트
 * - QuerydslConfiguration 자동 임포트
 * - EntityManager로 영속성 컨텍스트 제어
 * - 공통 헬퍼 메서드 제공
 *
 * 사용법:
 * <pre>
 * {@code
 * class EventRepositoryQueryDslTest extends BaseRepositoryTest {
 *
 *     @Autowired
 *     private EventRepository eventRepository;
 *
 *     @Test
 *     void testSomething() {
 *         Event event = persistAndFlush(EventFixture.builder().build());
 *         // 테스트 로직
 *     }
 * }
 * }
 * </pre>
 *
 * @author Claude Sonnet 4.5
 * @since 2026-02-10
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Autowired
    protected EntityManager em;

    /**
     * 엔티티를 영속화하고 플러시 + 캐시 초기화
     *
     * 사용 시나리오:
     * - 테스트 데이터를 생성하고 즉시 조회 쿼리를 테스트할 때
     * - 영속성 컨텍스트의 1차 캐시를 우회하여 실제 DB 조회를 보장
     *
     * @param entity 영속화할 엔티티
     * @param <T> 엔티티 타입
     * @return 영속화된 엔티티 (ID 할당됨)
     */
    protected <T> T persistAndFlush(T entity) {
        em.persist(entity);
        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화 (신선한 조회 보장)
        return entity;
    }

    /**
     * 엔티티를 영속화 (플러시/캐시 초기화 없음)
     *
     * 사용 시나리오:
     * - 여러 엔티티를 한번에 생성하고 마지막에 플러시할 때
     * - 배치 삽입 최적화
     *
     * @param entity 영속화할 엔티티
     * @param <T> 엔티티 타입
     * @return 영속화된 엔티티
     */
    protected <T> T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    /**
     * 플러시 후 캐시 초기화
     *
     * 사용 시나리오:
     * - persist()로 여러 엔티티를 생성한 후 일괄 플러시
     * - 쿼리 실행 전 변경사항을 DB에 반영하고 캐시 초기화
     */
    protected void flushAndClear() {
        em.flush();
        em.clear();
    }

    /**
     * 엔티티를 조회 (타입 + ID)
     *
     * @param entityClass 엔티티 클래스
     * @param id 엔티티 ID
     * @param <T> 엔티티 타입
     * @return 조회된 엔티티 (없으면 null)
     */
    protected <T> T find(Class<T> entityClass, Object id) {
        return em.find(entityClass, id);
    }

    /**
     * 영속성 컨텍스트 플러시
     *
     * 사용 시나리오:
     * - 쓰기 지연 SQL을 즉시 실행하고 싶을 때
     * - 쿼리 실행 전 변경사항을 DB에 반영
     */
    protected void flush() {
        em.flush();
    }

    /**
     * 영속성 컨텍스트 초기화
     *
     * 사용 시나리오:
     * - 1차 캐시를 비우고 실제 DB 조회를 강제할 때
     * - N+1 쿼리 테스트 시 캐시 영향 제거
     */
    protected void clear() {
        em.clear();
    }

    /**
     * 엔티티를 준영속 상태로 만듦
     *
     * @param entity 분리할 엔티티
     */
    protected void detach(Object entity) {
        em.detach(entity);
    }

    /**
     * 엔티티의 영속 상태 확인
     *
     * @param entity 확인할 엔티티
     * @return 영속 상태 여부
     */
    protected boolean contains(Object entity) {
        return em.contains(entity);
    }
}
