package com.blog.controller;

import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;
import com.blog.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Article>> findAll() {
        System.out.println( "ArticleController.findAll()");
        List<Article> response = articleService.findAll();
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") Long id) {
        try {
            Article article = articleService.findById(id);
            return ResponseEntity.status(HttpStatus.OK).body(article);
        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("L'article avec ID: "+id+" n'est pas disponible");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la recherche de l'article avec ID: "+id);
        }

    }

    @PostMapping("/save")
    public ResponseEntity<Long> save(@RequestBody ArticleSaveDTO article) {
        System.out.println("from controller" +article.toString());
        Long id = articleService.save(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id, @RequestBody ArticleSaveDTO article){
        Article updated = articleService.update(id, article);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<String> active(@PathVariable Long id, @RequestParam Boolean active){
        String response = articleService.active(id, active);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        articleService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }





}
