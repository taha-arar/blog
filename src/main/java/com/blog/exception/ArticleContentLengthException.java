package com.blog.exception;

import org.springframework.http.HttpStatus;

public class ArticleContentLengthException extends BlogException{

    public ArticleContentLengthException(){
        super("Content has to be between 5 and 10 characters", HttpStatus.BAD_REQUEST);
    }

}
