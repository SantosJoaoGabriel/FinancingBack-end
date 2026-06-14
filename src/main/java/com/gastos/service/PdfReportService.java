package com.gastos.service;

import com.gastos.model.ReportHistory;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class PdfReportService {

    public byte[] generateReportPdf(
            ReportHistory report,
            BigDecimal totalEntradas,
            BigDecimal totalSaidas,
            BigDecimal saldoFinal,
            List<ReportItemPdf> itens
    ) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            document.add(new Paragraph("SaveUp - Relatório Financeiro", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Título: " + report.title, subtitleFont));
            document.add(new Paragraph("Descrição: " + report.description, contentFont));
            document.add(new Paragraph("Período: " + report.period, contentFont));
            document.add(new Paragraph("Formato: " + report.format, contentFont));
            document.add(new Paragraph("Status: " + report.status.name(), contentFont));
            document.add(new Paragraph("Categoria: " + report.category.name(), contentFont));
            document.add(new Paragraph(
                    "Gerado em: " + report.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    contentFont
            ));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Resumo financeiro", sectionFont));
            document.add(new Paragraph("Total de entradas: R$ " + formatar(totalEntradas), contentFont));
            document.add(new Paragraph("Total de saídas: R$ " + formatar(totalSaidas), contentFont));
            document.add(new Paragraph("Saldo final: R$ " + formatar(saldoFinal), contentFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Detalhamento", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 2f, 2f});

            adicionarCabecalho(table, "Data", headerFont);
            adicionarCabecalho(table, "Descrição", headerFont);
            adicionarCabecalho(table, "Tipo", headerFont);
            adicionarCabecalho(table, "Valor", headerFont);

            if (itens == null || itens.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("Nenhum dado encontrado para o período.", contentFont));
                emptyCell.setColspan(4);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(8f);
                table.addCell(emptyCell);
            } else {
                for (ReportItemPdf item : itens) {
                    table.addCell(criarCelula(item.data(), contentFont));
                    table.addCell(criarCelula(item.descricao(), contentFont));
                    table.addCell(criarCelula(item.tipo(), contentFont));
                    table.addCell(criarCelula("R$ " + formatar(item.valor()), contentFont));
                }
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Este relatório foi gerado automaticamente pelo sistema SaveUp.", contentFont));

            document.close();
            return outputStream.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório", e);
        }
    }

    private void adicionarCabecalho(PdfPTable table, String texto, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(texto, font));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(6f);
        table.addCell(header);
    }

    private PdfPCell criarCelula(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", font));
        cell.setPadding(6f);
        return cell;
    }

    private String formatar(BigDecimal valor) {
        return valor == null ? "0,00" : String.format("%.2f", valor);
    }

    public record ReportItemPdf(
            String data,
            String descricao,
            String tipo,
            BigDecimal valor
    ) {
    }
}