package com.travel.journal.config;

import com.travel.journal.security.CustomOidcUserService;
import com.travel.journal.security.OAuth2AuthenticationSuccessHandler;
import com.travel.journal.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomOidcUserService customOidcUserService;
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(final JwtUtil jwtUtil, final CustomOidcUserService customOidcUserService) {
        this.jwtUtil = jwtUtil;
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/*","/error", "/api/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.baseUri("/api/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/api/auth/oauth/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService))
                        .successHandler(new OAuth2AuthenticationSuccessHandler(jwtUtil))
                        .failureHandler((request, response, exception) -> {
                            log.warn("OAuth2 login failed: " + exception.getMessage());
                        }));
        return http.build();

    }

}
