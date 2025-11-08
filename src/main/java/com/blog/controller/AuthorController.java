package com.blog.controller;

import com.blog.dto.AuthorSaveDTO;
import com.blog.service.AuthorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<Long> save(@RequestBody AuthorSaveDTO author){
        log.info("Received request to create author with email {}", author.getEmail());
        Long savedAuthor = authorService.save(author);
        log.info("Author created successfully with id {}", savedAuthor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Received request to delete author {}", id);
        authorService.delete(id);
        log.info("Author {} deleted successfully", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorSaveDTO> update(@PathVariable Long id, @RequestBody AuthorSaveDTO author) {
        log.info("Received request to update author {}", id);
        AuthorSaveDTO updated = authorService.update(id, author);
        log.info("Author {} updated successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<String> active(@PathVariable Long id, @RequestParam Boolean active) {
        log.info("Received request to change active flag for author {} to {}", id, active);
        String response = authorService.active(id, active);
        log.info("Active flag change processed for author {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorSaveDTO> findById(@PathVariable("id") Long id) {
        log.info("Received request to fetch author {}", id);
        AuthorSaveDTO author = authorService.findById(id);
        log.info("Author {} retrieved successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(author);
    }

    @GetMapping
    public ResponseEntity<List<AuthorSaveDTO>> findAll() {
        log.info("Received request to list all authors");
        List<AuthorSaveDTO> authors = authorService.findAll();
        if (authors.isEmpty()) {
            log.info("No authors found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authors);
        }
        log.info("Returning {} authors", authors.size());
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<AuthorSaveDTO>> findAllPagination(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Received request for paginated authors page={} size={} sortBy={} direction={}", page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<AuthorSaveDTO> authors = authorService.findAllPagination(pageable);
        if (authors.isEmpty()) {
            log.info("Paginated authors result is empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authors);
        }
        log.info("Returning {} authors for requested page", authors.getNumberOfElements());
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
        log.info("Received request for paginated authors with search page={} size={} sortBy={} direction={} criteria={}", page, size, sortBy, direction, criteria);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<AuthorSaveDTO> authors = authorService.findAllPaginationWithSearch(criteria, pageable);
        if (authors.isEmpty()) {
            log.info("Paginated authors search result is empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authors);
        }
        log.info("Returning {} authors for requested search page", authors.getNumberOfElements());
        return ResponseEntity.status(HttpStatus.OK).body(authors);
    }

}
