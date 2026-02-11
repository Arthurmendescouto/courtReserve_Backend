package com.example.court_reserve.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User", description = "Entidade que representa um usuário do sistema.")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "users")
@Builder(access = AccessLevel.PRIVATE)
public class User implements UserDetails {
    @Schema(description = "Identificador único do usuário.", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome do usuário.", example = "João Silva")
    @Column(nullable = false)
    private String name;

    @Schema(description = "E-mail do usuário.", example = "joao@email.com")
    @Column(nullable = false,unique = true)
    private String email;

    @Schema(description = "Senha do usuário (criptografada).", example = "senha123")
    @Column(nullable = false)
    private String password;

    // --- MÉTODOS DE DOMÍNIO (RICH MODEL) ---

    public static User create(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        
        user.validate();
        return user;
    }

    public void update(String name, String password) {
        if (name != null) this.name = name;
        if (password != null) this.password = password;
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        if (this.email == null || !this.email.contains("@")) {
            throw new IllegalArgumentException("O e-mail informado é inválido.");
        }
        if (this.password == null || this.password.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
    }

    // --- SPRING SECURITY ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    }

    @Override
    public boolean isEnabled() {
        return true;    }
}