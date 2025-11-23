package com.blog.dto;

import com.blog.model.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private Role role;



}
