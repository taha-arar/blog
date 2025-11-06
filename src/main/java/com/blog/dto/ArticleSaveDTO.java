package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArticleSaveDTO {

    @NotBlank(message = "Title is required")
    private String title;
    @Size(min = 5, max = 10, message = "Contenu has to be between 5 and 10 characters")
    private String content;
    @NotNull(message = "Author is required")
    private Long authorId;

}
