package com.sungbok.church.security.oauth;

import com.sungbok.church.domain.entity.User;
import com.sungbok.church.domain.enums.Provider;
import com.sungbok.church.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        log.info("OAuth2 Login Request - Provider: {}", registrationId);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes oauthAttributes = OAuthAttributes.of(
                registrationId, 
                userNameAttributeName, 
                attributes
        );

        User user = findOrCreateUser(oauthAttributes);

        log.info("OAuth2 Login Success - User: {}, Provider: {}", 
                user.getEmail(), user.getProvider());

        String authority = user.getRole().toAuthority();
        
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(authority)),
                attributes,
                userNameAttributeName
        );
    }

    @Transactional
    public User findOrCreateUser(OAuthAttributes attributes) {
        String oauthId = attributes.getUniqueKey();
        String email = attributes.getEmail();
        
        return userRepository.findByOauthId(oauthId)
                .map(user -> {
                    user.updateOAuthInfo(
                            attributes.getName(),
                            attributes.getEmail(),
                            attributes.getPicture()
                    );
                    return user;
                })
                .orElseGet(() -> {
                    if (email != null) {
                        return userRepository.findByEmail(email)
                                .map(existingUser -> linkOAuthToExistingUser(existingUser, attributes))
                                .orElseGet(() -> createNewUser(attributes));
                    }
                    return createNewUser(attributes);
                });
    }

    private User linkOAuthToExistingUser(User existingUser, OAuthAttributes attributes) {
        if (existingUser.getProvider() != null && 
            existingUser.getProvider() != attributes.getProvider()) {
            log.warn("이미 다른 소셜 계정으로 연결됨: {} -> {}", 
                    existingUser.getEmail(), existingUser.getProvider());
            return existingUser;
        }
        
        existingUser.setOAuthInfo(
                attributes.getProvider(),
                attributes.getId(),
                attributes.getUniqueKey()
        );
        existingUser.updateOAuthInfo(
                attributes.getName(),
                attributes.getEmail(),
                attributes.getPicture()
        );
        
        log.info("계정 통합 완료 - 일반->OAuth: {}, Provider: {}", 
                existingUser.getEmail(), attributes.getProvider());
        
        return existingUser;
    }

    private User createNewUser(OAuthAttributes attributes) {
        User user = User.createOAuthUser(
                attributes.getName(),
                attributes.getEmail(),
                attributes.getPicture(),
                attributes.getProvider(),
                attributes.getId()
        );

        return userRepository.save(user);
    }
}
