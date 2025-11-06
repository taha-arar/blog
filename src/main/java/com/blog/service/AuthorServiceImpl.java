package com.blog.service;

import com.blog.converter.AuthorMapper;
import com.blog.dto.AuthorSaveDTO;
import com.blog.model.Article;
import com.blog.model.Author;
import com.blog.repository.ArticleRepository;
import com.blog.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final ArticleRepository articleRepository;


    @Override
    public Long save(AuthorSaveDTO author) {
        if(authorRepository.existsByEmail(author.getEmail()))
            throw new IllegalArgumentException("Author with Email: "+author.getEmail()+" already exists");
        Author toBeSaved = authorMapper.toEntity(author);
        if (toBeSaved.getIsActive() == null) {
            toBeSaved.setIsActive(true);
        }
        return authorRepository.save(toBeSaved).getId();
    }

    @Override
    public void delete(Long id) {
        if(!authorRepository.existsById(id))
            throw new IllegalArgumentException("Author not found with id: "+id);
        authorRepository.deleteById(id);
    }

    @Transactional
    @Override
    public AuthorSaveDTO update(Long id, AuthorSaveDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + id));

        if (authorDTO.getEmail() != null && !authorDTO.getEmail().equalsIgnoreCase(author.getEmail())
                && authorRepository.existsByEmail(authorDTO.getEmail())) {
            throw new IllegalStateException("Author with Email: " + authorDTO.getEmail() + " already exists");
        }

        authorMapper.updateAuthorFromDTO(author, authorDTO);
        Author saved = authorRepository.save(author);
        return authorMapper.toDTO(saved);
    }

    @Override
    public String active(Long id, Boolean active) {
        if (active == null) {
            throw new IllegalStateException("Active flag is required");
        }

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + id));

        if (Boolean.valueOf(active).equals(author.getIsActive())) {
            return "Author already " + (Boolean.TRUE.equals(active) ? "active" : "deactivated");
        }

        author.setIsActive(Boolean.TRUE.equals(active));
        authorRepository.save(author);
        return "Author " + (Boolean.TRUE.equals(active) ? "activated" : "deactivated") + " successfully.";
    }

    @Override
    public AuthorSaveDTO findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + id));
        return authorMapper.toDTO(author);
    }

    @Override
    public List<AuthorSaveDTO> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toDTO)
                .toList();
    }

    @Override
    public Page<AuthorSaveDTO> findAllPagination(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(authorMapper::toDTO);
    }

    @Override
    public Page<AuthorSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable) {
        return authorRepository.findAllWithSearch(criteria, pageable)
                .map(authorMapper::toDTO);
    }

}
