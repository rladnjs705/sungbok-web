package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import com.sungbok.church.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

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
        if (name != null) {
            this.name = name;
        }
        if (email != null) {
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
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
}
