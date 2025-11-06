package com.blog.converter;

import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    Article toEntity(ArticleSaveDTO article);

//    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorId", expression = "java(article.getAuthor() != null ? article.getAuthor().getId() : null)")
    @Mapping(target = "authorFullName", expression = "java(article.getAuthor() != null ? article.getAuthor().getFirstName() + \" \" + article.getAuthor().getLastName() : null)")
    ArticleSaveDTO toDTO(Article article);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateArticleFromDTO(@MappingTarget Article article, ArticleSaveDTO articleDTO);
}
