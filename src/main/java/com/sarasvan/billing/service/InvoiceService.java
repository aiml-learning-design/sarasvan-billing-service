package com.sarasvan.billing.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.sarasvan.billing.model.Invoice;
import com.sarasvan.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository repository;

    public Invoice create(final Invoice invoice) {
        validateInvoice(invoice);
        generateInvoiceNumber(invoice);
        return repository.save(invoice);
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }

        if (invoice.getDueDate() == null && invoice.getInvoiceDate() != null) {
            invoice.setDueDate(invoice.getInvoiceDate().plusDays(30));
        }

        if (invoice.getAmount() == null || invoice.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invoice amount must be positive");
        }
    }

    private void calculateTaxes(Invoice invoice) {
        if (invoice.getGstRate() != null && invoice.getGstRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxableAmount = invoice.getAmount();
            BigDecimal taxAmount = taxableAmount.multiply(invoice.getGstRate()).divide(new BigDecimal(100));

            if ("INTER".equals(invoice.getGstStatus())) {
                invoice.setIgst(taxAmount);
                invoice.setCgst(BigDecimal.ZERO);
                invoice.setSgst(BigDecimal.ZERO);
            } else {
                invoice.setIgst(BigDecimal.ZERO);
                invoice.setCgst(taxAmount.divide(new BigDecimal(2)));
                invoice.setSgst(taxAmount.divide(new BigDecimal(2)));
            }
        }
    }

    private void generateInvoiceNumber(Invoice invoice) {
        if (invoice.getInvoiceNumber() == null) {
            String prefix = "SARASVAN-INV";
            String timestamp = String.valueOf(System.currentTimeMillis());
            invoice.setInvoiceNumber(prefix + "-" + timestamp.substring(5));
        }
    }


    public List<Invoice> list() {
        return repository.findByDeletedFalse();
    }

    public Optional<Invoice> get(Long id) {
        return repository.findById(id).filter(inv -> !inv.getDeleted());
    }

    public void delete(Long id) {
        repository.findById(id).ifPresent(inv -> {
            inv.setDeleted(true);
            repository.save(inv);
        });
    }

    public ByteArrayInputStream exportCsv() {
        List<Invoice> invoices = list();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("Invoice Number,Billed To,Currency,Amount,Status,Place Of Supply,Invoice Date,Due Date,Due Amount");
        for (Invoice inv : invoices) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    inv.getInvoiceNumber(), inv.getBilledTo(), inv.getCurrency(), inv.getAmount(),
                    inv.getStatus(), inv.getPlaceOfSupply(), inv.getInvoiceDate(),
                    inv.getDueDate(), inv.getDueAmount());
        }
        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }


    public ByteArrayInputStream exportPdf() throws IOException {
        List<Invoice> invoices = list();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph title = new Paragraph("Invoice Report")
                .setFont(titleFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Add generation date
        Paragraph generationDate = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
        document.add(generationDate);

        // Create table
        float[] columnWidths = {2, 3, 1, 2, 2, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        addTableHeader(table, "Invoice #", headerFont);
        addTableHeader(table, "Billed To", headerFont);
        addTableHeader(table, "Currency", headerFont);
        addTableHeader(table, "Amount", headerFont);
        addTableHeader(table, "Status", headerFont);
        addTableHeader(table, "Invoice Date", headerFont);
        addTableHeader(table, "Due Date", headerFont);
        addTableHeader(table, "Due Amount", headerFont);
        addTableHeader(table, "GST Status", headerFont);

        // Add invoice data
        PdfFont dataFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Invoice inv : invoices) {
            table.addCell(new Cell().add(new Paragraph(inv.getInvoiceNumber())).setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getBilledTo())).setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getCurrency())).setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getAmount().toString())).setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getStatus())).setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getInvoiceDate().format(dateFormatter))).setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getDueDate() != null ? inv.getDueDate().format(dateFormatter) : ""))
                    .setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getDueAmount() != null ? inv.getDueAmount().toString() : ""))
                    .setFont(dataFont));
            table.addCell(new Cell().add(new Paragraph(inv.getGstStatus())).setFont(dataFont));
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(Table table, String header, PdfFont font) {
        table.addCell(new Cell()
                .add(new Paragraph(header))
                .setFont(font)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER));
    }

    // Single invoice PDF export
    public ByteArrayInputStream exportInvoiceAsPdf(Long invoiceId) throws IOException {
        Invoice invoice = get(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Invoice header
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph title = new Paragraph("INVOICE")
                .setFont(titleFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Invoice details
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        float[] columnWidths = {3, 1, 3};
        Table detailsTable = new Table(UnitValue.createPercentArray(columnWidths));
        detailsTable.setWidth(UnitValue.createPercentValue(80));
        detailsTable.setMarginBottom(20);

        // Left column - Invoice info
        detailsTable.addCell(createDetailCell("Invoice Number:", boldFont));
        detailsTable.addCell(createDetailCell(invoice.getInvoiceNumber(), regularFont));

        detailsTable.addCell(createDetailCell("Invoice Date:", boldFont));
        detailsTable.addCell(createDetailCell(
                invoice.getInvoiceDate().format(dateFormatter), regularFont));

        detailsTable.addCell(createDetailCell("Due Date:", boldFont));
        detailsTable.addCell(createDetailCell(
                invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : "N/A",
                regularFont));

        detailsTable.addCell(createDetailCell("Billed To:", boldFont));
        detailsTable.addCell(new Cell(1, 2).add(new Paragraph(invoice.getBilledTo())).setFont(regularFont));

        Table amountTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
        amountTable.setWidth(UnitValue.createPercentValue(50));
        amountTable.setMarginTop(20);

        amountTable.addCell(createDetailCell("Subtotal:", boldFont));
        amountTable.addCell(createDetailCell(invoice.getAmount().toString(), regularFont));

        if (invoice.getIgst() != null && invoice.getIgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("IGST (" + invoice.getGstRate() + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoice.getIgst().toString(), regularFont));
        }

        if (invoice.getCgst() != null && invoice.getCgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("CGST (" + invoice.getGstRate().divide(BigDecimal.valueOf(2), RoundingMode.CEILING) + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoice.getCgst().toString(), regularFont));
        }

        if (invoice.getSgst() != null && invoice.getSgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("SGST (" + invoice.getGstRate().divide(BigDecimal.valueOf(2), RoundingMode.CEILING) + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoice.getSgst().toString(), regularFont));
        }

        amountTable.addCell(createDetailCell("Total:", boldFont).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        BigDecimal total = invoice.getAmount()
                .add(invoice.getIgst() != null ? invoice.getIgst() : BigDecimal.ZERO)
                .add(invoice.getCgst() != null ? invoice.getCgst() : BigDecimal.ZERO)
                .add(invoice.getSgst() != null ? invoice.getSgst() : BigDecimal.ZERO);
        amountTable.addCell(createDetailCell(total.toString(), regularFont)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        document.add(detailsTable);
        document.add(amountTable);

        Paragraph footer = new Paragraph("Thank you for your business!")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private Cell createDetailCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text)).setFont(font).setPadding(5);
    }
}