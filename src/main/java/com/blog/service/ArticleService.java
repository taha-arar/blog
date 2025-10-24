package com.blog.service;

import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

/*    List<Article> findAll();

    Article findById(Long id);

    Long save(ArticleSaveDTO article);

    Article update(Long id, ArticleSaveDTO article);

    String active(Long id, Boolean active);

    void delete(Long id);*/

//    ArticleSaveDTO save(ArticleSaveDTO article);

    Long save(ArticleSaveDTO article);

    ArticleSaveDTO update(Long id, ArticleSaveDTO article);

    String active(Long id, Boolean active);

    Article findById(Long id);

    List<ArticleSaveDTO> findAll();

    Page<ArticleSaveDTO> findAllPagination(Pageable pageable);

    Page<ArticleSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable);
}
