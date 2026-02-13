package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.Provider;
import com.sungbok.church.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * 사용자 엔티티
 * 
 * OAuth2 로그인 지원:
 * - provider: 소셜 로그인 제공자 (GOOGLE, NAVER, KAKAO)
 * - providerId: 소셜 제공자의 고유 ID
 * - oauthId: 낶부 관리용 고유 식별자 (provider + providerId)
 * 
 * 기본 로그인과 OAuth 로그인 모두 지원
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_oauth", columnList = "provider, provider_id", unique = true),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username", unique = true)
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * OAuth 제공자
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Provider provider;

    /**
     * OAuth 제공자의 고유 ID
     */
    @Column(name = "provider_id", length = 100)
    private String providerId;

    /**
     * 낶부 관리용 OAuth 고유 식별자
     * 예: GOOGLE_123456789
     */
    @Column(name = "oauth_id", length = 120, unique = true)
    private String oauthId;

    // ==================== 팩토리 메서드 ====================

    /**
     * OAuth 사용자 생성 팩토리 메서드
     * 
     * @param name 사용자 이름
     * @param email 이메일
     * @param profileImage 프로필 이미지 URL
     * @param provider OAuth 제공자
     * @param providerId 제공자별 고유 ID
     * @return User 엔티티
     */
    public static User createOAuthUser(String name, 
                                       String email, 
                                       String profileImage,
                                       Provider provider, 
                                       String providerId) {
        String oauthId = provider.name() + "_" + providerId;
        String username = generateOAuthUsername(provider, providerId);
        
        return User.builder()
                .username(username)
                .password(generateRandomPassword())  // OAuth는 비밀번호 불필요
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .provider(provider)
                .providerId(providerId)
                .oauthId(oauthId)
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    /**
     * 일반 회원가입용 팩토리 메서드
     */
    public static User createLocalUser(String username, 
                                       String password, 
                                       String name, 
                                       String email,
                                       String phone) {
        return User.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phone(phone)
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    // ==================== 비즈니스 메서드 ====================

    /**
     * OAuth 사용자 정보 업데이트
     * 로그인 시마다 최신 정보로 갱신
     */
    public void updateOAuthInfo(String name, String email, String profileImage) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }

    /**
     * OAuth 정보 설정 (계정 연결용)
     * 일반 계정 → OAuth 연결 시 사용
     */
    public void setOAuthInfo(Provider provider, String providerId, String oauthId) {
        this.provider = provider;
        this.providerId = providerId;
        this.oauthId = oauthId;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 사용자 정보 업데이트
     */
    public void updateInfo(String name, String email, String phone) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
    }

    /**
     * 프로필 이미지 업데이트
     */
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * 사용자 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 사용자 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 역할 변경
     */
    public void changeRole(UserRole newRole) {
        this.role = newRole;
    }

    /**
     * OAuth 사용자 여부 확인
     */
    public boolean isOAuthUser() {
        return this.provider != null;
    }

    // ==================== Private Helper Methods ====================

    /**
     * OAuth용 사용자명 생성
     * 중복 방지를 위해 provider prefix 추가
     */
    private static String generateOAuthUsername(Provider provider, String providerId) {
        String prefix = provider.getRegistrationId().substring(0, 3).toLowerCase();
        String shortId = providerId.length() > 8 
                ? providerId.substring(0, 8) 
                : providerId;
        return prefix + "_" + shortId + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * 랜덤 비밀번호 생성 (OAuth용)
     * 실제로는 사용되지 않음
     */
    private static String generateRandomPassword() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }
}
