package com.sungbok.church.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 인증 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type;
    private String username;
    private String role;

    public static AuthResponse of(String token, String username, String role) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(username)
                .role(role)
                .build();
    }
}
