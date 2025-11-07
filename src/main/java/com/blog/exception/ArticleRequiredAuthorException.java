package com.blog.exception;

import org.springframework.http.HttpStatus;

public class ArticleRequiredAuthorException extends BlogException{

    public ArticleRequiredAuthorException(){
        super("Author is required", HttpStatus.BAD_REQUEST);
    }

}
