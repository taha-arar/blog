package com.blog.service;

import com.blog.dto.AuthorSaveDTO;

public interface AuthorService {

    Long save(AuthorSaveDTO author);
}
