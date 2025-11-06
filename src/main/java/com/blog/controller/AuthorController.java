package com.blog.controller;

import com.blog.dto.AuthorSaveDTO;
import com.blog.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
