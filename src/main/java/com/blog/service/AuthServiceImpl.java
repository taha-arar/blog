package com.blog.service;

import com.blog.config.JwtService;
import com.blog.dto.AuthResponse;
import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.exception.UserDuplicateEmailException;
import com.blog.model.User;
import com.blog.model.enums.Role;
import com.blog.record.AuthenticationResult;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResult registre(RegisterRequest registerRequest) {
        log.info("Attempting to register  user {} ", registerRequest.getEmail());
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            log.warn("User with email {} already exists", registerRequest.getEmail());
            throw new UserDuplicateEmailException(registerRequest.getEmail());
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .isActive(registerRequest.getActive() != null ? registerRequest.getActive() : Boolean.FALSE)
                .role(Role.VISITOR)
                .biography(registerRequest.getBiography())
                .domain(registerRequest.getDomain())
                .build();
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        log.info("User {}  registered successfully", saved.getEmail());
        return new AuthenticationResult(toAuthResponse(saved), token);
    }

    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {
        log.info("Attempting to authenticate {}", loginRequest.getEmail());
        Authentication  authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        log.info("User {}  authenticated successfully", user.getEmail());
        return new AuthenticationResult(toAuthResponse(user), token);
    }


    private AuthResponse toAuthResponse(User user){
        return AuthResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getIsActive())
                .build();
    }
}
