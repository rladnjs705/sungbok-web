package com.sungbok.church.security.oauth;

import com.sungbok.church.security.jwt.JwtTokenProvider;
import com.sungbok.church.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    
    private static final String FRONTEND_URL = System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:3001");
    private static final String COOKIE_NAME = "ACCESS_TOKEN";
    private static final Duration COOKIE_MAX_AGE = Duration.ofMinutes(15);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        
        if (email == null) {
            log.error("OAuth2 로그인 실패: email이 null입니다");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided");
            return;
        }
        
        log.info("OAuth2 로그인 성공: email={}", email);
        
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        tokenService.createRefreshToken(email);

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .build();
        
        response.addHeader("Set-Cookie", cookie.toString());
        
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/oauth/callback")
                .queryParam("login", "success")
                .queryParam("email", email)
                .queryParam("name", name)
                .toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
