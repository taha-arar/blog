package com.blog.converter;

import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "author", ignore = true)
    Article toEntity(ArticleSaveDTO article);
    @Mapping(target = "author", ignore = true)
    ArticleSaveDTO toDTO(Article article);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", ignore = true)
    void updateArticleFromDTO(@MappingTarget Article article, ArticleSaveDTO articleDTO);
}
