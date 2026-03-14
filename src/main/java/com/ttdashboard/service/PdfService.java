package com.ttdashboard.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ttdashboard.entity.Project;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    public byte[] generatePdf(List<Project> projects) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("IP Management Dashboard Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph generatedOn = new Paragraph(
                    "Generated on: "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    subtitleFont);
            generatedOn.setSpacingAfter(12f);
            generatedOn.setAlignment(Element.ALIGN_RIGHT);
            document.add(generatedOn);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {2, 3, 3, 4});

            addHeaderCell(table, "Roll Number");
            addHeaderCell(table, "Student Name");
            addHeaderCell(table, "Project Name");
            addHeaderCell(table, "GitHub Repo");

            for (Project project : projects) {
                addBodyCell(table, safe(project.getRollNumber()));
                addBodyCell(table, safe(project.getStudentName()));
                addBodyCell(table, safe(project.getName()));
                addBodyCell(table, safe(project.getGithubRepo()));
            }

            document.add(table);

            Paragraph footer = new Paragraph(
                    "Total records: " + projects.size() + " | Date: " + LocalDate.now(),
                    subtitleFont);
            footer.setSpacingBefore(10f);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Failed to generate PDF report", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Unexpected error while creating PDF", ex);
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text) {
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(text, bodyFont));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private String safe(String v) {
        return v == null || v.isEmpty() ? "-" : v;
    }
}
