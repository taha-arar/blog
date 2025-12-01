package com.blog.controller;

import com.blog.dto.AuthorSaveDTO;
import com.blog.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/v1/authors")
@AllArgsConstructor
@Slf4j
@Tag(name = "Authors", description = "Operations related to author management")
public class AuthorController {

    private final AuthorService authorService;

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Create a new author", description = "Creates an author with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid author data"),
            @ApiResponse(responseCode = "409", description = "Author email already exists")
    })
    public ResponseEntity<Long> save(@RequestBody AuthorSaveDTO author){
        log.info("Received request to create author with email {}", author.getEmail());
        Long savedAuthor = authorService.save(author);
        log.info("Author created successfully with id {}", savedAuthor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Deletes an author identified by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Received request to delete author {}", id);
        authorService.delete(id);
        log.info("Author {} deleted successfully", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an author", description = "Updates an author identified by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid author data"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "409", description = "Author email already exists")
    })
    public ResponseEntity<AuthorSaveDTO> update(@PathVariable Long id, @RequestBody AuthorSaveDTO author) {
        log.info("Received request to update author {}", id);
        AuthorSaveDTO updated = authorService.update(id, author);
        log.info("Author {} updated successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @PatchMapping("/active/{id}")
    @Operation(summary = "Activate or deactivate an author", description = "Update the active flag for an author")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Active flag not provided"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<String> active(@PathVariable Long id, @RequestParam Boolean active) {
        log.info("Received request to change active flag for author {} to {}", id, active);
        String response = authorService.active(id, active);
        log.info("Active flag change processed for author {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or (hasRole('AUTHOR') and #id == principal.id)")
    @GetMapping("/{id}")
    @Operation(summary = "Find author by ID", description = "Retrieves an author using its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<AuthorSaveDTO> findById(@PathVariable("id") Long id) {
        log.info("Received request to fetch author {}", id);
        AuthorSaveDTO author = authorService.findById(id);
        log.info("Author {} retrieved successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(author);
    }

    @GetMapping
    @Operation(summary = "List all authors", description = "Retrieves all authors without pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authors retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No authors found")
    })
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
    @Operation(summary = "Paginated authors", description = "Retrieve authors with pagination parameters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated authors retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No authors found for the requested page")
    })
    public ResponseEntity<Page<AuthorSaveDTO>> findAllPagination(
            @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "Number of elements per page", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "3") Integer size,
            @Parameter(name = "sortBy", description = "Sorting field", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(name = "direction", description = "Sorting direction", in = ParameterIn.QUERY)
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
    @Operation(summary = "Paginated search of authors", description = "Retrieve paginated authors filtered by search criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated search results retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No authors found for the given search criteria")
    })
    public ResponseEntity<Page<AuthorSaveDTO>> findAllPaginationWithSearch(
            @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(name = "size", description = "Number of elements per page", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "3") Integer size,
            @Parameter(name = "sortBy", description = "Sorting field", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(name = "direction", description = "Sorting direction", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "asc") String direction,
            @Parameter(name = "criteria", description = "Optional search keyword", in = ParameterIn.QUERY)
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
