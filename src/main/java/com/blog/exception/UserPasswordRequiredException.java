package com.blog.exception;

import org.springframework.http.HttpStatus;

public class UserPasswordRequiredException extends BlogException {

    public UserPasswordRequiredException() {
        super("Le mot de passe est obligatoire pour créer un utilisateur", HttpStatus.BAD_REQUEST);
    }
}

