package com.blog.exception;

import org.springframework.http.HttpStatus;

public class AuthorNotFoundException extends BlogException{

    public AuthorNotFoundException(Long authorId){
        super("Author not found with id: "+authorId, HttpStatus.NOT_FOUND);
    }

}
