package com.sungbok.church.service;

import com.sungbok.church.api.dto.request.LoginRequest;
import com.sungbok.church.api.dto.request.RegisterRequest;
import com.sungbok.church.api.dto.response.AuthResponse;
import com.sungbok.church.domain.entity.User;
import com.sungbok.church.domain.enums.UserRole;
import com.sungbok.church.domain.repository.UserRepository;
import com.sungbok.church.exception.BusinessException;
import com.sungbok.church.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // JWT 토큰 생성
        String token = tokenProvider.createToken(authentication);

        // 사용자 정보 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다"));

        log.info("User logged in: {}", request.getUsername());

        return AuthResponse.of(token, user.getUsername(), user.getRole().name());
    }

    /**
     * 회원가입
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("이미 사용 중인 사용자명입니다");
        }

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("이미 사용 중인 이메일입니다");
        }

        // 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .isActive(true)
                .build();

        userRepository.save(user);

        log.info("New user registered: {}", user.getUsername());

        // 자동 로그인
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = tokenProvider.createToken(authentication);

        return AuthResponse.of(token, user.getUsername(), user.getRole().name());
    }
}
