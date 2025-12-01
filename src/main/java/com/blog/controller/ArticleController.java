package com.blog.controller;

import com.blog.dto.ArticleSaveDTO;
import com.blog.dto.AuthorAssignmentRequest;
import com.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@Slf4j
@Tag(name = "Articles", description = "Operations related to article management")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'AUTHOR')")
    @PostMapping
    @Operation(summary = "Create a new article", description = "Creates an article with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Article created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid article data"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "409", description = "Article title already exists")
    })
    public ResponseEntity<Object> save(@RequestBody ArticleSaveDTO article) {
        log.info("Received request to create article with title {}", article.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.save(article));
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'AUTHOR')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing article", description = "Updates an article identified by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid article data"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ResponseEntity<ArticleSaveDTO> update(@PathVariable Long id, @RequestBody ArticleSaveDTO article){
        log.info("Received request to update article {} with payload", id);
        ArticleSaveDTO updated = articleService.update(id, article);
        log.info("Article {} updated successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @PatchMapping("/active/{id}")
    @Operation(summary = "Activate or deactivate an article", description = "Update the active flag for an article")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Requested state already applied"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ResponseEntity<String> active(@PathVariable Long id, @RequestParam Boolean active){
        log.info("Received request to change active flag for article {} to {}", id, active);
        return ResponseEntity.ok(articleService.active(id, active));
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @PatchMapping("/{id}/author")
    @Operation(summary = "Assign an author to an article", description = "Associates an author to an existing article")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Article or author not found"),
            @ApiResponse(responseCode = "409", description = "Author already assigned to this article")
    })
    public ResponseEntity<ArticleSaveDTO> assignAuthor(@PathVariable Long id, @RequestBody AuthorAssignmentRequest request){
        log.info("Received request to assign author {} to article {}", request.getAuthorId(), id);
        ArticleSaveDTO updated = articleService.assignAuthor(id, request.getAuthorId());
        log.info("Author {} assigned to article {}", request.getAuthorId(), id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    @Operation(summary = "Find article by ID", description = "Retrieves an article using its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ResponseEntity<ArticleSaveDTO> findById(@PathVariable("id") Long id) {
        log.info("Received request to fetch article {}", id);
        ArticleSaveDTO article = articleService.findById(id);
        log.info("Article {} retrieved successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(article);
    }

    @GetMapping
    @Operation(summary = "List all articles", description = "Retrieves all articles without pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No articles found")
    })
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
    @Operation(summary = "Paginated articles", description = "Retrieve articles with pagination parameters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated articles retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No articles found for the requested page")
    })
    public ResponseEntity<Page<ArticleSaveDTO>> findAllPagination(
            @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "Number of elements per page", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "3") Integer size,
            @Parameter(name = "sortBy", description = "Sorting field", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "id") String sortBy,
            @Parameter(name = "direction", description = "Sorting direction", in = ParameterIn.QUERY)
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
    @Operation(summary = "Paginated search of articles", description = "Retrieve paginated articles filtered by search criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated search results retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No articles found for the given search criteria")
    })
    public ResponseEntity<Page<ArticleSaveDTO>> findAllPaginationWithSearch(
            @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "Number of elements per page", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "3") Integer size,
            @Parameter(name = "sortBy", description = "Sorting field", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "id") String sortBy,
            @Parameter(name = "direction", description = "Sorting direction", in = ParameterIn.QUERY)
            @RequestParam (defaultValue = "asc") String direction,
            @Parameter(name = "criteria", description = "Optional search keyword", in = ParameterIn.QUERY)
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

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @PatchMapping("/{articleId}/author/{authorId}")
    @Operation(summary = "Assign an author to an article", description = "Assigns an author using path variables for article and author IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Article or author not found"),
            @ApiResponse(responseCode = "409", description = "Author already assigned to this article")
    })
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
