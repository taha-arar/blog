package com.blog.repository;

import com.blog.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Author a " +
            "WHERE " +
            "(:criteria IS NULL OR :criteria = ''" +
            "OR a.firstName LIKE %:criteria% " +
            "OR a.lastName LIKE %:criteria% " +
            "OR a.email LIKE %:criteria% " +
            "OR CAST(a.id AS string) = :criteria)")
    Page<Author> findAllWithSearch(@Param("criteria") String criteria, Pageable pageable);
}
