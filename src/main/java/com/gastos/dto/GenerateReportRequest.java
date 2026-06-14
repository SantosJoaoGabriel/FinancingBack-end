package com.gastos.dto;

import jakarta.validation.constraints.NotBlank;

public class GenerateReportRequest {

    @NotBlank(message = "O tipo do relatório é obrigatório")
    public String type;
}