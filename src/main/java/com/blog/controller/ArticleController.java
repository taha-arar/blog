package com.blog.controller;

import com.blog.dto.ArticleSaveDTO;
import com.blog.dto.AuthorAssignmentRequest;
import com.blog.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/articles")
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @PostMapping
    public ResponseEntity<Object> save(@RequestBody ArticleSaveDTO article) {
        log.info("Received request to create article with title {}", article.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.save(article));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleSaveDTO> update(@PathVariable Long id, @RequestBody ArticleSaveDTO article){
        log.info("Received request to update article {} with payload", id);
        ArticleSaveDTO updated = articleService.update(id, article);
        log.info("Article {} updated successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<String> active(@PathVariable Long id, @RequestParam Boolean active){
        log.info("Received request to change active flag for article {} to {}", id, active);
        return ResponseEntity.ok(articleService.active(id, active));
    }

    @PatchMapping("/{id}/author")
    public ResponseEntity<ArticleSaveDTO> assignAuthor(@PathVariable Long id, @RequestBody AuthorAssignmentRequest request){
        log.info("Received request to assign author {} to article {}", request.getAuthorId(), id);
        ArticleSaveDTO updated = articleService.assignAuthor(id, request.getAuthorId());
        log.info("Author {} assigned to article {}", request.getAuthorId(), id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleSaveDTO> findById(@PathVariable("id") Long id) {
        log.info("Received request to fetch article {}", id);
        ArticleSaveDTO article = articleService.findById(id);
        log.info("Article {} retrieved successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(article);
    }

    @GetMapping
    public ResponseEntity<List<ArticleSaveDTO>> findAll() {
        log.info("Received request to list all articles");
        List<ArticleSaveDTO> articles = articleService.findAll();
        if(articles.isEmpty()){
            log.info("No articles found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(articles);
        } else {
            log.info("Returning {} articles", articles.size());
            return ResponseEntity.status(HttpStatus.OK).body(articles);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ArticleSaveDTO>> findAllPagination(
            @RequestParam (defaultValue = "0") Integer page,
            @RequestParam (defaultValue = "3") Integer size,
            @RequestParam (defaultValue = "id") String sortBy,
            @RequestParam (defaultValue = "asc") String direction
    ){
        log.info("Received request for paginated articles page={} size={} sortBy={} direction={}", page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<ArticleSaveDTO> articles = articleService.findAllPagination(pageable);
        if(articles.isEmpty()){
            log.info("Paginated articles result is empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(articles);
        } else {
            log.info("Returning {} articles for requested page", articles.getNumberOfElements());
            return ResponseEntity.status(HttpStatus.OK).body(articles);
        }

    }

    @GetMapping("/page-search")
    public ResponseEntity<Page<ArticleSaveDTO>> findAllPaginationWithSearch(
            @RequestParam (defaultValue = "0") Integer page,
            @RequestParam (defaultValue = "3") Integer size,
            @RequestParam (defaultValue = "id") String sortBy,
            @RequestParam (defaultValue = "asc") String direction,
            @RequestParam (required = false) String criteria
    ){
        log.info("Received request for paginated articles with search page={} size={} sortBy={} direction={} criteria={}", page, size, sortBy, direction, criteria);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<ArticleSaveDTO> articles = articleService.findAllPaginationWithSearch(criteria, pageable);
        if(articles.isEmpty()){
            log.info("Paginated search result is empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(articles);
        } else {
            log.info("Returning {} articles for requested search page", articles.getNumberOfElements());
            return ResponseEntity.status(HttpStatus.OK).body(articles);
        }

    }

    @PatchMapping("/{articleId}/author/{authorId}")
    public ResponseEntity<ArticleSaveDTO> assigneAuthor(@PathVariable Long articleId, @PathVariable Long authorId){
        log.info("Received request to assign author {} to article {}", authorId, articleId);
        ArticleSaveDTO updatedArticle = articleService.assignAuthor(articleId, authorId);
        log.info("Author {} assigned to article {}", authorId, articleId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedArticle);
    }

/*    @GetMapping("/page-search")
    public ResponseEntity<Page<ArticleSaveDTO>> findAllPaginationWithSearch(
            @RequestParam (defaultValue = "0") Integer page,
            @RequestParam (defaultValue = "3") Integer size,
            @RequestParam (defaultValue = "id") String sortBy,
            @RequestParam (defaultValue = "asc") String direction,
            @RequestParam (required = false) String criteria
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<ArticleSaveDTO> articles = articleService.findAllPaginationWithSearch(criteria, pageable);
        if(articles.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(articles);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(articles);
        }

    }*/


/*    @PostMapping
    public ResponseEntity<Object> save(@RequestBody ArticleSaveDTO article){
        try {
            ArticleSaveDTO savedArticle = articleService.save(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }*/

/*
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
*/





}
