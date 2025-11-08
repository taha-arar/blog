package com.blog.exception;

import org.springframework.http.HttpStatus;

public class AuthorDuplicateEmailException extends BlogException {

    public AuthorDuplicateEmailException(String email) {
        super("Author with email: " + email + " already exists", HttpStatus.CONFLICT);
    }
}

