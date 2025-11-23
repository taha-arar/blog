package com.blog.record;

import com.blog.dto.AuthResponse;

public record AuthenticationResult(AuthResponse user, String token) {
}
