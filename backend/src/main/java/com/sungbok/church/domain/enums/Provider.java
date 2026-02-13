package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * OAuth2 제공자 Enum
 * 
 * Provider별 특성:
 * - GOOGLE: 표준 OAuth2, sub 필드 사용
 * - NAVER: response 래핑 필요, id 필드 사용
 * - KAKAO: kakao_account 래핑 필요, id 필드 사용
 */
@Getter
@RequiredArgsConstructor
public enum Provider {
    
    GOOGLE("google", "구글", "sub", "email", "name", "picture"),
    NAVER("naver", "네이버", "id", "email", "name", "profile_image"),
    KAKAO("kakao", "카카오", "id", "email", "nickname", "profile_image_url");
    
    private final String registrationId;
    private final String displayName;
    private final String idAttributeKey;
    private final String emailAttributeKey;
    private final String nameAttributeKey;
    private final String pictureAttributeKey;
    
    /**
     * registrationId로 Provider 조회
     */
    public static Provider from(String registrationId) {
        for (Provider provider : values()) {
            if (provider.registrationId.equalsIgnoreCase(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }
    
    /**
     * DB 저장용 키
     */
    public String getKey() {
        return name();
    }
}
