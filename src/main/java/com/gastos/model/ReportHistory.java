package com.gastos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_history")
public class ReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 150)
    public String title;

    @Column(nullable = false, length = 255)
    public String description;

    @Column(nullable = false, length = 100)
    public String period;

    @Column(nullable = false, length = 10)
    public String format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public ReportStatus status;

    @Column(nullable = false, length = 150)
    public String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public ReportCategory category;

    @Column(nullable = false)
    public LocalDateTime createdAt;

    @Column(nullable = false)
    public Long userId;

    public enum ReportStatus {
        CONCLUIDO,
        PROCESSANDO
    }

    public enum ReportCategory {
        FINANCEIRO,
        ANALITICO,
        ANUAL
    }
}