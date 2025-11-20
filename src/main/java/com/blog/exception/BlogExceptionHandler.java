package com.blog.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class BlogExceptionHandler {

    @ExceptionHandler(BlogException.class)
    public ResponseEntity<ApiError> handleBlogException(BlogException ex, HttpServletRequest request){
        HttpStatus status = ex.getHttpStatus();
        ApiError error = new ApiError(
               Instant.now(),
               status.value(),
               status.getReasonPhrase(),
               ex.getMessage(),
               request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}
