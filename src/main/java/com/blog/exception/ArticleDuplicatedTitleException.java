package com.blog.exception;

import org.springframework.http.HttpStatus;

public class ArticleDuplicatedTitleException extends BlogException{

    public ArticleDuplicatedTitleException(String title){
        super("Article with title ' "+title+"' already exists", HttpStatus.CONFLICT);
    }

}
