package com.gastos.service;

import com.gastos.model.ReportHistory;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class PdfReportService {

    public byte[] generateReportPdf(ReportHistory report) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("SaveUp - Relatório Financeiro", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Título: " + report.title, subtitleFont));
            document.add(new Paragraph("Descrição: " + report.description, contentFont));
            document.add(new Paragraph("Período: " + report.period, contentFont));
            document.add(new Paragraph("Formato: " + report.format, contentFont));
            document.add(new Paragraph("Status: " + report.status.name(), contentFont));
            document.add(new Paragraph("Categoria: " + report.category.name(), contentFont));
            document.add(new Paragraph("Gerado em: " +
                    report.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), contentFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Este relatório foi gerado automaticamente pelo sistema SaveUp.", contentFont));

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório", e);
        }
    }
}