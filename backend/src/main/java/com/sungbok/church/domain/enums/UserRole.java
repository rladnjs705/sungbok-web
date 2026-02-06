package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 역할
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String description;

    /**
     * Spring Security 권한 형식으로 변환
     */
    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}
