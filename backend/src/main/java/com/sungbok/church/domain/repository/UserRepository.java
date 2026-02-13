package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 * 
 * OAuth2 로그인 지원 메서드:
 * - findByOauthId: OAuth 고유 ID로 조회
 * - findByEmail: 이메일로 조회 (계정 통합용)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명 중복 체크
     */
    boolean existsByUsername(String username);

    /**
     * 이메일로 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 중복 체크
     */
    boolean existsByEmail(String email);

    /**
     * OAuth 고유 ID로 조회
     * 예: GOOGLE_123456789
     */
    Optional<User> findByOauthId(String oauthId);

    /**
     * OAuth ID 존재 여부 확인
     */
    boolean existsByOauthId(String oauthId);
}
