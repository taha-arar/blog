package com.blog.dto;

import com.blog.model.enums.Domain;
import com.blog.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSaveDTO {

    private Long id;

    @NotBlank(message = "Le prenom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'Email doit être valide")
    private String email;

    @Size(max = 500, message = "La biographie ne peut pas dépasser 500 caractères")
    private String biography;

    @Column
    private Domain domain;

    private Boolean active;

    @Size(min = 8, message = "Le  mot  de passe doit contenir au moins 8 caractères")
    private String password;

    private Role role;
}

