package com.gastos.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class User extends PanacheEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 120)
    public String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true, length = 150)
    public String email;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false, length = 255)
    public String password;
}