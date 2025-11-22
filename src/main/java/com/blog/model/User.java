package com.blog.model;

import com.blog.model.enums.Domain;
import com.blog.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractEntity<Long> implements UserDetails {

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

    @Column(nullable = false)
    @NotBlank(message = "Le  mot de passe ne peut  pas être vide")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8  caractères")
    @JsonIgnore
    private  String  password;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "La biographie ne peut pas dépasser 500 caractères")
    private String biography;

    @Enumerated(EnumType.STRING)
    private Domain domain;

    @OneToMany(mappedBy = "author")
    private List<Article> articles;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}
