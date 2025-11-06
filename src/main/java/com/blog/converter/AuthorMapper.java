package com.blog.converter;

import com.blog.dto.AuthorSaveDTO;
import com.blog.model.Author;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", source = "active", defaultValue = "true")
    Author toEntity(AuthorSaveDTO author);

    @Mapping(target = "active", source = "isActive")
    AuthorSaveDTO toDTO(Author author);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", source = "active")
    void updateAuthorFromDTO(@MappingTarget Author author, AuthorSaveDTO authorDTO);

}
