package com.travel.journal.security;

import com.travel.journal.dto.UserDto;
import com.travel.journal.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    public OAuth2AuthenticationSuccessHandler(final JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException{
        CustomOidcUser customOidcUser = (CustomOidcUser) authentication.getPrincipal();
        UserDto userDto = customOidcUser.getUserDto();
        String refresh = jwtUtil.generateRefreshToken(userDto);
        ResponseCookie refreshCookie = ResponseCookie.from("refresh", refresh)
                                                     .httpOnly(true)
                                                     .secure(false)
                                                     .path("/api/auth")
                                                     .maxAge(Duration.ofDays(7))
                                                     .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String redirectUrl = "http://localhost:3000/auth/login-success";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
