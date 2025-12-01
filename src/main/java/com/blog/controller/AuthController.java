package com.blog.controller;

import com.blog.config.JwtProperties;
import com.blog.dto.AuthResponse;
import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.model.User;
import com.blog.record.AuthenticationResult;
import com.blog.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
 @RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        AuthenticationResult authenticationResult = authService.registre(registerRequest);
        log.info("User registered successfully");
        ResponseCookie cookie =buildTokenCookie(authenticationResult.token());
        return ResponseEntity.status(201)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authenticationResult.user());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest){
        AuthenticationResult authenticationResult = authService.login(loginRequest);
        log.info("User  {} logged in successfully", loginRequest.getEmail());
        ResponseCookie cookie =buildTokenCookie(authenticationResult.token());
        return ResponseEntity.status(200)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authenticationResult.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        ResponseCookie cookie = clearTokenCookie();
        log.info("User logged out, clearing JWT cookie");
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();

    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> currentUser(Authentication authentication) {
        if(authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("User {} retrieved successfully", user.getEmail());
        AuthResponse authResponse = AuthResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .active(user.getIsActive())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(authResponse);
    }

    private ResponseCookie buildTokenCookie(String token) {
        return ResponseCookie.from(jwtProperties.getCookieName(), token)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite(jwtProperties.getCookieSameSite())
                .path(jwtProperties.getCookiePath())
                .maxAge(jwtProperties.getExpirationMinutes()
        ).build();
    }

    private ResponseCookie clearTokenCookie() {
        return ResponseCookie.from(jwtProperties.getCookieName(), "")
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite(jwtProperties.getCookieSameSite())
                .path(jwtProperties.getCookiePath())
                .maxAge(jwtProperties.getExpirationMinutes()
                ).build();
    }
}
