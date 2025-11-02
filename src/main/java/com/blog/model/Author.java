package com.blog.model;

import com.blog.model.enums.Domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Author extends AbstractEntity<Long>{

    @NotBlank(message = "Le prenom ne peut pas être vide")
    @Size(min = 2, message = "Le prenom doit être plus que deux caractères")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, message = "Le nom doit être plus que deux caractères")
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    private String email;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "La biographie ne peut pas dépasser 500 caractères")
    private String biography;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Domain domain;

    @OneToMany(mappedBy = "author")
    private List<Article> articles;
}
