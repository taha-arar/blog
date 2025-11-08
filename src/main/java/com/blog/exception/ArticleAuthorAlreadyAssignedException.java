package com.blog.exception;

import org.springframework.http.HttpStatus;

public class ArticleAuthorAlreadyAssignedException extends BlogException {

    public ArticleAuthorAlreadyAssignedException(Long articleId, Long authorId) {
        super("Author with ID: " + authorId + " is already assigned to article with ID: " + articleId, HttpStatus.CONFLICT);
    }
}

