package com.sungbok.church.domain.repository;

import com.sungbok.church.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
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
     * 이메일 중복 체크
     */
    boolean existsByEmail(String email);
}
