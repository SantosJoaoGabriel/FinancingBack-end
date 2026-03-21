package com.gastos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
public class Transacao extends PanacheEntity {

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false)
    public String descricao;

    @NotBlank(message = "Categoria é obrigatória")
    @Column(nullable = false)
    public String categoria;

    @NotNull(message = "Data é obrigatória")
    @Column(nullable = false)
    public LocalDate data;

    @NotNull(message = "Valor é obrigatório")
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal valor;

    @Column
    public String paymentMethod;

    @Column(length = 500)
    public String notes;
}
