package com.blog.service;

import com.blog.dto.UserSaveDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    Long save(UserSaveDTO author);

    void delete(Long id);

    UserSaveDTO update(Long id, UserSaveDTO author);

    String active(Long id, Boolean active);

    UserSaveDTO findById(Long id);

    List<UserSaveDTO> findAll();

    Page<UserSaveDTO> findAllPagination(Pageable pageable);

    Page<UserSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable);

}
