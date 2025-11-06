package com.blog.service;

import com.blog.converter.AuthorMapper;
import com.blog.dto.AuthorSaveDTO;
import com.blog.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;


    @Override
    public Long save(AuthorSaveDTO author) {
        if(authorRepository.existsByEmail(author.getEmail()))
            throw new IllegalArgumentException("Author with Email: "+author.getEmail()+" already exists");
        System.out.println("From Service: "+authorMapper.toEntity(author).toString());
        return authorRepository.save(authorMapper.toEntity(author)).getId();
    }

    @Override
    public void delete(Long id) {
        if(!authorRepository.existsById(id))
            throw new IllegalArgumentException("Author not found with id: "+id);
        authorRepository.deleteById(id);
    }
}
