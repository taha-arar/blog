package com.blog.service;

import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

    List<Article> findAll();

    Article findById(Long id);

    Long save(ArticleSaveDTO article);

    Article update(Long id, ArticleSaveDTO article);

    String active(Long id, Boolean active);

    void delete(Long id);
}
