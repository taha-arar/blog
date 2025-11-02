package com.blog.converter;

import com.blog.dto.AuthorSaveDTO;
import com.blog.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "articles" , ignore = true)
    Author toEntity(AuthorSaveDTO author);

    AuthorSaveDTO toDTO(Author author);


}
