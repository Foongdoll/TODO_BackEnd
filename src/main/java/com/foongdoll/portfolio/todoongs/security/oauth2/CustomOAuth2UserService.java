package com.foongdoll.portfolio.todoongs.security.oauth2;

import com.foongdoll.portfolio.todoongs.api.entity.Users;
import com.foongdoll.portfolio.todoongs.api.repository.UsersRepository;
import com.foongdoll.portfolio.todoongs.security.AuthProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final OAuth2Error EMAIL_NOT_FOUND_ERROR =
            new OAuth2Error("invalid_email", "Email not found from OAuth2 provider", null);

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuthAttributes attributes = OAuthAttributes.of(
                userRequest.getClientRegistration().getRegistrationId(),
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(),
                oAuth2User.getAttributes()
        );

        if (attributes.getEmail() == null || attributes.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(EMAIL_NOT_FOUND_ERROR);
        }

        Users user = saveOrUpdate(attributes);

        Map<String, Object> enriched = attributes.toAttributeMapWithUser(user);

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                enriched,
                attributes.getNameAttributeKey()
        );
    }

    private Users saveOrUpdate(OAuthAttributes attributes) {
        return usersRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
                .map(existing -> {
                    existing.setName(attributes.getName());
                    existing.setPictureUrl(attributes.getPicture());
                    existing.setEmail(attributes.getEmail());
                    return usersRepository.save(existing);
                })
                .orElseGet(() -> usersRepository.save(attributes.toEntity()));
    }
}
