package com.blog.service;

import com.blog.dto.AuthorSaveDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    Long save(AuthorSaveDTO author);

    void delete(Long id);

    AuthorSaveDTO update(Long id, AuthorSaveDTO author);

    String active(Long id, Boolean active);

    AuthorSaveDTO findById(Long id);

    List<AuthorSaveDTO> findAll();

    Page<AuthorSaveDTO> findAllPagination(Pageable pageable);

    Page<AuthorSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable);

}
