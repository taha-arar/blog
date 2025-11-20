package com.blog.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BlogException{

    public UserNotFoundException(String username){
        super("User  not  found  with  email: "+username, HttpStatus.NOT_FOUND);
    }

}
