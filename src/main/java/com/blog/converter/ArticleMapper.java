package com.blog.converter;

import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    Article toEntity(ArticleSaveDTO article);

    ArticleSaveDTO toDTO(Article article);
}
