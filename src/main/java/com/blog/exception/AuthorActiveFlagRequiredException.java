package com.blog.exception;

import org.springframework.http.HttpStatus;

public class AuthorActiveFlagRequiredException extends BlogException {

    public AuthorActiveFlagRequiredException() {
        super("Active flag is required", HttpStatus.BAD_REQUEST);
    }
}

