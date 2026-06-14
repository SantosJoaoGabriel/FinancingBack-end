package com.gastos.service;

import com.gastos.dto.GenerateReportRequest;
import com.gastos.dto.ReportHistoryResponse;
import com.gastos.model.ReportHistory;
import com.gastos.model.Transaction;
import com.gastos.model.TransactionType;
import com.gastos.repository.ReportHistoryRepository;
import com.gastos.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportService {

    @Inject
    ReportHistoryRepository reportHistoryRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    PdfReportService pdfReportService;

    @Transactional
    public ReportHistoryResponse generateReport(GenerateReportRequest request, Long userId) {
        ReportHistory report = new ReportHistory();

        report.title = getTitleByType(request.type);
        report.description = getDescriptionByType(request.type);
        report.period = buildPeriodLabelByType(request.type);
        report.format = "PDF";
        report.status = ReportHistory.ReportStatus.CONCLUIDO;
        report.fileName = buildFileName(request.type);
        report.category = getCategoryByType(request.type);
        report.createdAt = LocalDateTime.now();
        report.userId = userId;

        reportHistoryRepository.persist(report);

        return toResponse(report);
    }

    public List<ReportHistoryResponse> listHistory(Long userId) {
        return reportHistoryRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public byte[] downloadReport(Long reportId, Long userId) {
        ReportHistory report = reportHistoryRepository.findById(reportId);

        if (report == null) {
            throw new RuntimeException("Relatório não encontrado");
        }

        if (!report.userId.equals(userId)) {
            throw new RuntimeException("Acesso negado ao relatório");
        }

        DateRange range = resolvePeriod(report);
        List<Transaction> transactions = transactionRepository.findByUserIdAndPeriod(userId, range.startDate(), range.endDate());

        BigDecimal totalEntradas = transactions.stream()
                .filter(t -> t.type == TransactionType.INCOME)
                .map(t -> t.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = transactions.stream()
                .filter(t -> t.type == TransactionType.EXPENSE)
                .map(t -> t.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = totalEntradas.subtract(totalSaidas);

        List<PdfReportService.ReportItemPdf> itens = buildItems(report, transactions);

        return pdfReportService.generateReportPdf(
                report,
                totalEntradas,
                totalSaidas,
                saldoFinal,
                itens
        );
    }

    public ReportHistoryResponse findById(Long reportId, Long userId) {
        ReportHistory report = reportHistoryRepository.findById(reportId);

        if (report == null) {
            throw new RuntimeException("Relatório não encontrado");
        }

        if (!report.userId.equals(userId)) {
            throw new RuntimeException("Acesso negado ao relatório");
        }

        return toResponse(report);
    }

    @Transactional
    public void deleteReport(Long reportId, Long userId) {
        ReportHistory report = reportHistoryRepository.findById(reportId);

        if (report == null) {
            throw new RuntimeException("Relatório não encontrado");
        }

        if (!report.userId.equals(userId)) {
            throw new RuntimeException("Acesso negado ao relatório");
        }

        reportHistoryRepository.delete(report);
    }

    private List<PdfReportService.ReportItemPdf> buildItems(ReportHistory report, List<Transaction> transactions) {
        if (report.category == ReportHistory.ReportCategory.ANALITICO) {
            Map<String, BigDecimal> porCategoria = transactions.stream()
                    .filter(t -> t.type == TransactionType.EXPENSE)
                    .collect(Collectors.groupingBy(
                            t -> t.category,
                            Collectors.mapping(
                                    t -> t.amount,
                                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                            )
                    ));

            return porCategoria.entrySet().stream()
                    .map(entry -> new PdfReportService.ReportItemPdf(
                            "",
                            entry.getKey(),
                            "CATEGORIA",
                            entry.getValue()
                    ))
                    .toList();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return transactions.stream()
                .map(t -> new PdfReportService.ReportItemPdf(
                        t.date.format(formatter),
                        t.description,
                        t.type.name(),
                        t.amount
                ))
                .toList();
    }

    private DateRange resolvePeriod(ReportHistory report) {
        LocalDate hoje = LocalDate.now();

        return switch (report.category) {
            case FINANCEIRO -> new DateRange(
                    hoje.withDayOfMonth(1),
                    hoje.withDayOfMonth(hoje.lengthOfMonth())
            );
            case ANALITICO -> new DateRange(
                    hoje.minusDays(30),
                    hoje
            );
            case ANUAL -> new DateRange(
                    Year.now().atDay(1),
                    Year.now().atMonth(12).atEndOfMonth()
            );
        };
    }

    private ReportHistoryResponse toResponse(ReportHistory report) {
        ReportHistoryResponse response = new ReportHistoryResponse();
        response.id = report.id;
        response.date = report.createdAt.toLocalDate().toString();
        response.title = report.title;
        response.description = report.description;
        response.period = report.period;
        response.format = report.format;
        response.status = formatStatus(report.status);
        response.fileName = report.fileName;
        response.category = formatCategory(report.category);
        return response;
    }

    private String getTitleByType(String type) {
        return switch (type) {
            case "MONTHLY" -> "Relatório mensal";
            case "CATEGORY" -> "Gastos por categoria";
            case "ANNUAL" -> "Resumo anual";
            default -> throw new IllegalArgumentException("Tipo de relatório inválido");
        };
    }

    private String getDescriptionByType(String type) {
        return switch (type) {
            case "MONTHLY" -> "Conciliação completa de entradas e saídas do mês vigente.";
            case "CATEGORY" -> "Distribuição percentual de despesas por categoria.";
            case "ANNUAL" -> "Visão consolidada das movimentações financeiras do ano.";
            default -> throw new IllegalArgumentException("Tipo de relatório inválido");
        };
    }

    private String buildPeriodLabelByType(String type) {
        LocalDate hoje = LocalDate.now();

        return switch (type) {
            case "MONTHLY" -> hoje.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " - " +
                    hoje.withDayOfMonth(hoje.lengthOfMonth()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case "CATEGORY" -> hoje.minusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " - " +
                    hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case "ANNUAL" -> "01/01/" + hoje.getYear() + " - 31/12/" + hoje.getYear();
            default -> throw new IllegalArgumentException("Tipo de relatório inválido");
        };
    }

    private String buildFileName(String type) {
        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return switch (type) {
            case "MONTHLY" -> "relatorio-mensal-" + data + ".pdf";
            case "CATEGORY" -> "gastos-por-categoria-" + data + ".pdf";
            case "ANNUAL" -> "resumo-anual-" + data + ".pdf";
            default -> throw new IllegalArgumentException("Tipo de relatório inválido");
        };
    }

    private ReportHistory.ReportCategory getCategoryByType(String type) {
        return switch (type) {
            case "MONTHLY" -> ReportHistory.ReportCategory.FINANCEIRO;
            case "CATEGORY" -> ReportHistory.ReportCategory.ANALITICO;
            case "ANNUAL" -> ReportHistory.ReportCategory.ANUAL;
            default -> throw new IllegalArgumentException("Tipo de relatório inválido");
        };
    }

    private String formatStatus(ReportHistory.ReportStatus status) {
        return switch (status) {
            case CONCLUIDO -> "Concluído";
            case PROCESSANDO -> "Processando";
        };
    }

    private String formatCategory(ReportHistory.ReportCategory category) {
        return switch (category) {
            case FINANCEIRO -> "Financeiro";
            case ANALITICO -> "Analítico";
            case ANUAL -> "Anual";
        };
    }

    private record DateRange(LocalDate startDate, LocalDate endDate) {
    }
}