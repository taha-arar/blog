package com.blog.config;

import com.blog.model.User;
import com.blog.model.enums.Role;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SuperAdminBootstrap {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner superAdminSeeder(){
        return args ->  {
            String email = "anais@blog.com";
            if(userRepository.existsByEmail(email)){
                log.info("Super admin {} already exists", email);
                return;
            }
            log.info("Creating super admin {}", email);
            User superAdmin = User.builder()
                    .firstName("Anais")
                    .lastName("Garcia")
                    .email(email)
                    .password(passwordEncoder.encode("anais123"))
                    .role(Role.SUPERADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(superAdmin);
            log.info("Super admin {} created successfully", email);
        };
    }
}
