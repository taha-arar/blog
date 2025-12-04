package com.blog.controller;

import com.blog.dto.UserSaveDTO;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping
    public ResponseEntity<Long> save(@RequestBody UserSaveDTO user){
        log.info("Attempting to save user with email {}", user.getEmail());
        Long savedUser = userService.save(user);
        log.info("User created successfully with id {}", savedUser);
        return ResponseEntity.status(201).body(savedUser);
    }
}
