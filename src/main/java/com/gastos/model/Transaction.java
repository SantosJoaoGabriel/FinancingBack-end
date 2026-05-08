package com.gastos.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
public class Transaction extends PanacheEntity {

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false)
    public String description;

    @NotBlank(message = "Categoria é obrigatória")
    @Column(nullable = false)
    public String category;

    @NotNull(message = "Data é obrigatória")
    @Column(nullable = false)
    public LocalDate date;

    @NotNull(message = "Valor é obrigatório")
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal amount;

    @Column
    public String paymentMethod;

    @Column(length = 500)
    public String notes;
}