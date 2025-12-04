package com.blog.exception;

import org.springframework.http.HttpStatus;

public class UserValidEmailException extends BlogException {

    public UserValidEmailException() {
        super("User email should be valid", HttpStatus.BAD_REQUEST);
    }
}

