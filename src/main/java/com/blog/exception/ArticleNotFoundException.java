package com.blog.exception;

import org.springframework.http.HttpStatus;

public class ArticleNotFoundException extends BlogException{


    public ArticleNotFoundException(Long articleId) {
        super("Article not found with ID: "+articleId, HttpStatus.NOT_FOUND);
    }
}
