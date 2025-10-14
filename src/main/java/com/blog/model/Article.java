package com.blog.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Article extends AbstractEntity<Long>{

    @NotBlank(message = "Le titre ne peut pas être vide")
    @Size(min = 3, message = "Le titre doit être plus que deux caractère")
    @Column(unique = true, nullable = false)
    private String title;

    @NotBlank(message = "Le contenu ne peut pas être vide")
    @Size(min = 5, max = 10, message = "Contenu has to be between 5 and 10 characters")
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotBlank(message = "L'auteur ne peut pas être vide")
    private String author;

    @NotNull(message = "Le statut doit être spécifier")
    private Boolean isActive = true;

}
