package com.blog.exception;

import org.springframework.http.HttpStatus;

public class AuthorPasswordRequiredException extends BlogException {

    public AuthorPasswordRequiredException() {
        super("Le mot de passe est obligatoire pour creer un auteur", HttpStatus.BAD_REQUEST);
    }
}

