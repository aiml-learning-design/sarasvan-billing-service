package com.sarasvan.billing.security.service;

import com.sarasvan.billing.entity.UserEntity;
import com.sarasvan.billing.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
                new org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService();
        OAuth2User oauthUser = delegate.loadUser(request);

        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Auto-register user
        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    // newUser.setPassword("GOOGLE");
             //       newUser.setFullName(name);
               //     newUser.setUserRole("USER");
                    return userRepository.save(newUser);
                });

        return new DefaultOAuth2User(
                oauthUser.getAuthorities(),
                attributes,
                "email"
        );
    }
}
