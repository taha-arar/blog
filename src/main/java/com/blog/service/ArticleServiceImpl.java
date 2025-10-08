package com.blog.service;

import com.blog.model.Article;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    List<Article> articles = Arrays.asList(
            new Article(1L, new Date(), null,  "Spring Boot Basics", "Learn how to build REST APIs with Spring Boot.", "Taha Arar"),
            new Article(2L, new Date(), null, "Understanding Dependency Injection", "Explaining DI and its role in Spring.", "John Doe"),
            new Article(3L, new Date(), null, "Spring Boot Security", "Secure your REST APIs with JWT and cookies.", "Jane Smith"),
            new Article(4L,  new Date(), null,"Spring Data JPA", "Simplify persistence layer with JPA repositories.", "Ali Ben Salem")
    );

//        List<Article> articles = new ArrayList<>();

    public ArticleServiceImpl() {}

    @Override
    public List<Article> findAll() {
        return articles;
    }

    @Override
    public Article findById(Long id) {
        return articles.stream().filter(
                article -> article.getId().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Article not found"));
    }

}
