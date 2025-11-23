package com.blog.service;

import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.record.AuthenticationResult;

public interface AuthService {

    AuthenticationResult registre(RegisterRequest registerRequest);

    AuthenticationResult login(LoginRequest loginRequest);
}
