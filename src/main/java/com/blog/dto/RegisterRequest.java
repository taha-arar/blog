package com.blog.dto;

import com.blog.model.enums.Domain;
import com.blog.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

     @NotBlank(message = "Le prénom est obligatoire")
     private String firstName;

     @NotBlank(message = "Le nom est obligatoire")
     private String lastName;

     @NotBlank(message = "L'email est obligatoire")
     @Email(message = "L'email doit être valide")
     private String email;

     @NotBlank(message = "Le  mot de passe ne peut  pas être vide")
     @Size(min = 8, message = "Le mot de passe doit contenir au moins 8  caractères")
     private String password;

     private Role role;

     private Boolean active;

     private String biography;

     private Domain domain;
}
