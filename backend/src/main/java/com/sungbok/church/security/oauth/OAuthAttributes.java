package com.sungbok.church.security.oauth;

import com.sungbok.church.domain.enums.Provider;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * OAuth2 사용자 속성 추상화 클래스
 * 
 * 각 제공자(Google, Naver, Kakao)의 다른 응답 구조를 
 * 공통 인터페이스로 변환하는 Adapter 패턴 적용
 * 
 * Provider별 응답 구조:
 * - Google: {sub, email, name, picture}
 * - Naver: {response: {id, email, name, profile_image}}
 * - Kakao: {id, kakao_account: {email, profile: {nickname, profile_image_url}}}
 */
@Getter
public class OAuthAttributes {
    
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final Provider provider;
    
    @Builder
    public OAuthAttributes(Map<String, Object> attributes, 
                          String nameAttributeKey, 
                          String name, 
                          String email, 
                          String picture,
                          Provider provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }
    
    /**
     * Provider별로 적절한 메서드 호출
     */
    public static OAuthAttributes of(String registrationId, 
                                     String userNameAttributeName, 
                                     Map<String, Object> attributes) {
        Provider provider = Provider.from(registrationId);
        
        return switch (provider) {
            case GOOGLE -> ofGoogle(userNameAttributeName, attributes, provider);
            case NAVER -> ofNaver(userNameAttributeName, attributes, provider);
            case KAKAO -> ofKakao(userNameAttributeName, attributes, provider);
        };
    }
    
    /**
     * Google OAuth2 응답 파싱
     */
    private static OAuthAttributes ofGoogle(String userNameAttributeName, 
                                            Map<String, Object> attributes,
                                            Provider provider) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(provider)
                .build();
    }
    
    /**
     * Naver OAuth2 응답 파싱
     * Naver는 response 객체 낶에 실제 데이터가 있음
     */
    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(String userNameAttributeName, 
                                           Map<String, Object> attributes,
                                           Provider provider) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)  // response 낶의 데이터만 저장
                .nameAttributeKey("id")
                .provider(provider)
                .build();
    }
    
    /**
     * Kakao OAuth2 응답 파싱
     * Kakao는 kakao_account 안에 email, profile 정보가 있음
     */
    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(String userNameAttributeName, 
                                           Map<String, Object> attributes,
                                           Provider provider) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(provider)
                .build();
    }
    
    /**
     * OAuth 제공자 ID (sub, id 등)
     */
    public String getId() {
        return String.valueOf(attributes.get(nameAttributeKey));
    }
    
    /**
     * 고유 식별자 (provider + id)
     * 예: GOOGLE_123456789
     */
    public String getUniqueKey() {
        return provider.name() + "_" + getId();
    }
}
