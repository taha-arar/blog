package com.blog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BlogException extends RuntimeException{

    private final HttpStatus httpStatus;

    protected BlogException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
