package com.blog.exception;

import org.springframework.http.HttpStatus;

public class UserDuplicateEmailException extends BlogException {

    public UserDuplicateEmailException(String email) {
        super("User with email: " + email + " already exists", HttpStatus.CONFLICT);
    }
}

