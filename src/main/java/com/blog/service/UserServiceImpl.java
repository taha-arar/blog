package com.blog.service;

import com.blog.dto.AuthorSaveDTO;
import com.blog.exception.AuthorDuplicateEmailException;
import com.blog.exception.AuthorPasswordRequiredException;
import com.blog.exception.UserValidEmailException;
import com.blog.model.User;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
 @RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long save(AuthorSaveDTO userTO) {
        log.info("Attempting to save user with email {}", userTO.getEmail());

        if(userTO.getEmail() == null || userTO.getEmail().isEmpty()) {
            log.warn("Email is required to save user");
            throw new UserValidEmailException();
        }

        if(userRepository.existsByEmail(userTO.getEmail())) {
            log.warn("User with email {} already exists", userTO.getEmail());
            throw new AuthorDuplicateEmailException(userTO.getEmail());
        }

        if(userTO.getPassword() == null || userTO.getPassword().isEmpty()) {
            log.warn("Password is required to save user");
            throw new AuthorPasswordRequiredException();
        }

        User user = User.builder()
                .firstName(userTO.getFirstName())
                .lastName(userTO.getLastName())
                .email(userTO.getEmail())
                .password(passwordEncoder.encode(userTO.getPassword()))
                .isActive(userTO.getActive() != null ? userTO.getActive() : Boolean.TRUE)
                .role(userTO.getRole())
                .biography(userTO.getBiography())
                .domain(userTO.getDomain())
                .build();

        Long id = userRepository.save(user).getId();
        log.info("User {} saved successfully with id {}", userTO.getEmail(), id);
        return id;
    }
}
