package com.gastos.resource;

import com.gastos.dto.GenerateReportRequest;
import com.gastos.dto.ReportHistoryResponse;
import com.gastos.service.ReportService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/reports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    ReportService reportService;

    @GET
    @Path("/history")
    public List<ReportHistoryResponse> getHistory() {
        Long userId = getAuthenticatedUserId();
        return reportService.listHistory(userId);
    }

    @POST
    @Path("/generate")
    public Response generate(@Valid GenerateReportRequest request) {
        Long userId = getAuthenticatedUserId();
        ReportHistoryResponse response = reportService.generateReport(request, userId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}/download")
    @Produces("application/pdf")
    public Response download(@PathParam("id") Long id) {
        Long userId = getAuthenticatedUserId();

        byte[] pdfBytes = reportService.downloadReport(id, userId);
        ReportHistoryResponse report = reportService.findById(id, userId);

        return Response.ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=\"" + report.fileName + "\"")
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Long userId = getAuthenticatedUserId();
        reportService.deleteReport(id, userId);
        return Response.noContent().build();
    }

    private Long getAuthenticatedUserId() {
        String subject = jwt.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new WebApplicationException("Claim sub não encontrado no token", 401);
        }

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Claim sub inválido para id do usuário", 401);
        }
    }
}