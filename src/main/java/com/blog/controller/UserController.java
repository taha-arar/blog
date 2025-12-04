package com.blog.controller;

import com.blog.dto.UserSaveDTO;
import com.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a user with the provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "User email already exists")
    })
    public ResponseEntity<Long> save(@RequestBody UserSaveDTO user){
        log.info("Received request to create user with email {}", user.getEmail());
        Long savedUser = userService.save(user);
        log.info("User created successfully with id {}", savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates a user identified by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User email already exists")
    })
    public ResponseEntity<UserSaveDTO> update(@PathVariable Long id, @RequestBody UserSaveDTO user) {
        log.info("Received request to update user {}", id);
        UserSaveDTO updated = userService.update(id, user);
        log.info("User {} updated successfully", id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user identified by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Received request to delete user {}", id);
        userService.delete(id);
        log.info("User {} deleted successfully", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @GetMapping
    @Operation(summary = "List all users", description = "Retrieves all users without pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No users found")
    })
    public ResponseEntity<List<UserSaveDTO>> findAll() {
        log.info("Received request to list all users");
        List<UserSaveDTO> users = userService.findAll();
        if (users.isEmpty()) {
            log.info("No users found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(users);
        }
        log.info("Returning {} users", users.size());
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @GetMapping("/page-search")
    @Operation(summary = "Paginated search of users", description = "Retrieve paginated users filtered by search criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated search results retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No users found for the given search criteria")
    })
    public ResponseEntity<Page<UserSaveDTO>> findAllPaginationWithSearch(
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
        log.info("Received request for paginated users with search page={} size={} sortBy={} direction={} criteria={}", page, size, sortBy, direction, criteria);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<UserSaveDTO> users = userService.findAllPaginationWithSearch(criteria, pageable);
        if (users.isEmpty()) {
            log.info("Paginated users search result is empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(users);
        }
        log.info("Returning {} users for requested search page", users.getNumberOfElements());
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
