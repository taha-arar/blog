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
    public ResponseEntity<Long> save(@RequestBody AuthorSaveDTO author){
        Long savedAuthor = authorService.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        authorService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorSaveDTO> update(@PathVariable Long id, @RequestBody AuthorSaveDTO author) {
        AuthorSaveDTO updated = authorService.update(id, author);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<String> active(@PathVariable Long id, @RequestParam Boolean active) {
        String response = authorService.active(id, active);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorSaveDTO> findById(@PathVariable("id") Long id) {
        AuthorSaveDTO author = authorService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(author);
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
