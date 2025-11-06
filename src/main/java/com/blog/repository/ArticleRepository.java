package com.blog.repository;

import com.blog.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByTitle(String title);


    @Query("SELECT art FROM Article art " +
            "WHERE " +
            "(:criteria IS NULL OR :criteria = ''" +
            "OR art.title LIKE %:criteria% " +
            "OR art.content LIKE %:criteria% " +
            "OR art.author.firstName LIKE %:criteria% " +
            "OR art.author.lastName LIKE %:criteria% " +
            "or CAST(art.id AS string) = :criteria)")
    Page<Article> findAllWithSearch(@Param("criteria") String criteria, Pageable pageable);

}
