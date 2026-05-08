package com.gastos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {

    public Long id;

    @NotBlank(message = "Descrição é obrigatória")
    public String description;

    @NotBlank(message = "Categoria é obrigatória")
    public String category;

    @NotNull(message = "Data é obrigatória")
    public LocalDate date;

    @NotNull(message = "Valor é obrigatório")
    public BigDecimal amount;

    public String paymentMethod;

    public String notes;
}