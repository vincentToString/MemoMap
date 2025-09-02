package com.travel.journal.controller;

import com.travel.journal.dto.UserDto;
import com.travel.journal.security.CustomOidcUserService;
import com.travel.journal.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final CustomOidcUserService customOidcUserService;
    public AuthController(JwtUtil jwtUtil, CustomOidcUserService customOidcUserService) {
        this.jwtUtil = jwtUtil;
        this.customOidcUserService = customOidcUserService;
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refresh", required = false) String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken) || refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid refresh token");
        } else {
            String email = jwtUtil.extractEmail(refreshToken);
            UserDto userDto = customOidcUserService.loadUserByEmail(email);
            if(userDto == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid refresh token");
            }
            String newAccessToken = jwtUtil.generateToken(userDto);

            return ResponseEntity.ok(Map.of(
                    "access", newAccessToken,
                    "displayName", userDto.getDisplayName(),
                    "email", userDto.getEmail(),
                    "joinedAt", userDto.getJoinedAt()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // all we need to do in the backend for logging a user out is to replace(clear) the refresh token.
        ResponseCookie expiredCookie = ResponseCookie.from("refresh", "")
                                                     .httpOnly(true)
                                                     .secure(true)
                                                     .sameSite("None")
                                                     .path("/api/auth")
                                                     .maxAge(0) // Expire immediately
                                                     .build();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                             .body("Logged out successfully");
    }
}
