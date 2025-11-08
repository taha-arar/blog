package com.blog.exception;

import org.springframework.http.HttpStatus;

public class ArticleStateAlreadySetException extends BlogException{

    public ArticleStateAlreadySetException(Long articleId, boolean state) {
        super("Article with ID: "+articleId+ " is already " + (state ? "active" : "inactive"), HttpStatus.CONFLICT);
    }
}
