package com.queddeng.oauth2login.service;

import com.queddeng.oauth2login.model.AuthProvider;
import com.queddeng.oauth2login.model.User;
import com.queddeng.oauth2login.repository.AuthProviderRepository;
import com.queddeng.oauth2login.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;
    private final OidcUserService oidcUserService = new OidcUserService();

    public CustomOAuth2UserService(UserRepository userRepository, AuthProviderRepository authProviderRepository) {
        this.userRepository = userRepository;
        this.authProviderRepository = authProviderRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes;

        try {
            // ✅ FIX: Only cast when it's Google (OIDC provider)
            if ("google".equals(registrationId)) {
                OidcUserRequest oidcRequest = (OidcUserRequest) userRequest; // ✅ cast properly
                OidcUser oidcUser = oidcUserService.loadUser(oidcRequest);
                attributes = oidcUser.getAttributes();
            } else {
                OAuth2User oAuth2User = super.loadUser(userRequest);
                attributes = oAuth2User.getAttributes();
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load user info: " + e.getMessage(), e);
        }

        String email = null;
        String name = null;
        String picture = null;
        String providerUserId = null;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");
            providerUserId = (String) attributes.get("sub");
        } else if ("github".equals(registrationId)) {
            email = (String) attributes.get("email");
            if (email == null && attributes.get("login") != null) {
                email = attributes.get("login") + "@github.com";
            }
            name = (String) attributes.get("name");
            if (name == null) name = (String) attributes.get("login");
            picture = (String) attributes.get("avatar_url");
            providerUserId = String.valueOf(attributes.get("id"));
        }

        System.out.println("🔍 OAuth2 Attributes: " + attributes);
        System.out.println("💾 Saving user: " + name + " (" + email + ")");

        // Save or update User
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setEmail(email);
        user.setDisplayName(name);
        user.setAvatarUrl(picture);
        user.setUpdatedAt(LocalDateTime.now());
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Save or update AuthProvider
        AuthProvider authProvider = authProviderRepository
                .findByProviderAndProviderUserId(registrationId.toUpperCase(), providerUserId)
                .orElseGet(AuthProvider::new);

        authProvider.setProvider(registrationId.toUpperCase());
        authProvider.setProviderUserId(providerUserId);
        authProvider.setProviderEmail(email);
        authProvider.setUser(user);
        authProviderRepository.save(authProvider);

        System.out.println("✅ User + AuthProvider saved to database.");

        // Return the proper user (OIDC or OAuth2)
        return "google".equals(registrationId)
                ? new DefaultOAuth2UserService().loadUser(userRequest)
                : super.loadUser(userRequest);
    }
}
