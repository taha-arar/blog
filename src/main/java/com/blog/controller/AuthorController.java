package com.blog.controller;

import com.blog.dto.AuthorSaveDTO;
import com.blog.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody AuthorSaveDTO author){
        try {
            Long savedAuthor = authorService.save(author);
            return ResponseEntity.status(201).body(savedAuthor);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        try {
            authorService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody AuthorSaveDTO author) {
        try {
            AuthorSaveDTO updated = authorService.update(id, author);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<Object> active(@PathVariable Long id, @RequestParam Boolean active) {
        try {
            String response = authorService.active(id, active);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") Long id) {
        try {
            AuthorSaveDTO author = authorService.findById(id);
            return ResponseEntity.status(HttpStatus.OK).body(author);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping
    public ResponseEntity<List<AuthorSaveDTO>> findAll() {
        List<AuthorSaveDTO> authors = authorService.findAll();
        if (authors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authors);
        }
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<AuthorSaveDTO>> findAllPagination(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<AuthorSaveDTO> authors = authorService.findAllPagination(pageable);
        if (authors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authors);
        }
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @GetMapping("/page-search")
    public ResponseEntity<Page<AuthorSaveDTO>> findAllPaginationWithSearch(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String criteria
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<AuthorSaveDTO> authors = authorService.findAllPaginationWithSearch(criteria, pageable);
        if (authors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authors);
        }
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

}
