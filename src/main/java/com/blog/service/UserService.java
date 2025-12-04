package com.blog.service;

import com.blog.dto.AuthorSaveDTO;

public interface UserService {

    Long save(AuthorSaveDTO user);
}
