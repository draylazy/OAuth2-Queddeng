package com.queddeng.oauth2login.config;

import com.queddeng.oauth2login.service.CustomOAuth2UserService;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF config (ignore for H2 console)
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/h2-console/**")
            )
            // Allow H2 console frames
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/oauth2/**", "/error", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            // OAuth2 login setup
            .oauth2Login(oauth -> oauth
                .loginPage("/")
                .userInfoEndpoint(u -> u.userService(oAuth2UserService))
                .defaultSuccessUrl("/profile", true)
            )
            // Logout setup
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
