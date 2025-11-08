package com.blog.service;

import com.blog.converter.AuthorMapper;
import com.blog.dto.AuthorSaveDTO;
import com.blog.exception.AuthorActiveFlagRequiredException;
import com.blog.exception.AuthorDuplicateEmailException;
import com.blog.exception.AuthorNotFoundException;
import com.blog.model.Author;
import com.blog.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;


    @Override
    public Long save(AuthorSaveDTO author) {
        log.info("Attempting to save author with email {}", author.getEmail());
        if(authorRepository.existsByEmail(author.getEmail())) {
            log.warn("Author with email {} already exists", author.getEmail());
            throw new AuthorDuplicateEmailException(author.getEmail());
        }
        Author toBeSaved = authorMapper.toEntity(author);
        if (toBeSaved.getIsActive() == null) {
            log.debug("Author {} has no active flag set, defaulting to true", author.getEmail());
            toBeSaved.setIsActive(true);
        }
        Long id = authorRepository.save(toBeSaved).getId();
        log.info("Author {} saved successfully with id {}", author.getEmail(), id);
        return id;
    }

    @Override
    public void delete(Long id) {
        log.info("Attempting to delete author {}", id);
        if(!authorRepository.existsById(id)) {
            log.warn("Author {} not found for deletion", id);
            throw new AuthorNotFoundException(id);
        }
        authorRepository.deleteById(id);
        log.info("Author {} deleted successfully", id);
    }

    @Transactional
    @Override
    public AuthorSaveDTO update(Long id, AuthorSaveDTO authorDTO) {
        log.info("Attempting to update author {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        if (authorDTO.getEmail() != null && !authorDTO.getEmail().equalsIgnoreCase(author.getEmail())
                && authorRepository.existsByEmail(authorDTO.getEmail())) {
            log.warn("Duplicate email {} detected during update for author {}", authorDTO.getEmail(), id);
            throw new AuthorDuplicateEmailException(authorDTO.getEmail());
        }

        authorMapper.updateAuthorFromDTO(author, authorDTO);
        Author saved = authorRepository.save(author);
        log.info("Author {} updated successfully", id);
        return authorMapper.toDTO(saved);
    }

    @Override
    public String active(Long id, Boolean active) {
        log.info("Updating active state for author {} to {}", id, active);
        if (active == null) {
            log.warn("Active flag required when updating author {}", id);
            throw new AuthorActiveFlagRequiredException();
        }

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        if (active.equals(author.getIsActive())) {
            log.warn("Active state already {} for author {}", active, id);
            return "Author already " + (active ? "active" : "deactivated");
        }

        author.setIsActive(active);
        authorRepository.save(author);
        log.info("Author {} active state updated to {}", id, active);
        return "Author " + (active ? "activated" : "deactivated") + " successfully.";
    }

    @Override
    public AuthorSaveDTO findById(Long id) {
        log.info("Fetching author {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        log.info("Author {} fetched successfully", id);
        return authorMapper.toDTO(author);
    }

    @Override
    public List<AuthorSaveDTO> findAll() {
        log.info("Fetching all authors");
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toDTO)
                .toList();
    }

    @Override
    public Page<AuthorSaveDTO> findAllPagination(Pageable pageable) {
        log.info("Fetching paginated authors page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return authorRepository.findAll(pageable)
                .map(authorMapper::toDTO);
    }

    @Override
    public Page<AuthorSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable) {
        log.info("Fetching paginated authors with search criteria={} page={} size={}", criteria, pageable.getPageNumber(), pageable.getPageSize());
        return authorRepository.findAllWithSearch(criteria, pageable)
                .map(authorMapper::toDTO);
    }

}
