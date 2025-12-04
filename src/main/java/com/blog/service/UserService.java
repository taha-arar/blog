package com.blog.service;

import com.blog.dto.UserSaveDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Long save(UserSaveDTO user);

    void delete(Long id);

    UserSaveDTO update(Long id, UserSaveDTO user);

    List<UserSaveDTO> findAll();

    Page<UserSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable);
}
