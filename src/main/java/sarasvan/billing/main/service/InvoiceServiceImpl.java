package sarasvan.billing.main.service;

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
import sarasvan.billing.main.entity.InvoiceDetailsEntity;
import sarasvan.billing.main.mapper.InvoiceDetailsMapper;
import sarasvan.billing.main.model.InvoiceDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarasvan.billing.main.repository.InvoiceDetailsRepository;

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

    private final InvoiceDetailsRepository invoiceDetailsRepository;

    public InvoiceDetails create(final InvoiceDetails invoiceDetails) {
        validateInvoice(invoiceDetails);
        generateInvoiceNumber(invoiceDetails);
        InvoiceDetailsEntity invoiceDetailsEntity =  InvoiceDetailsMapper.INSTANCE.dtoToEntity(invoiceDetails);
        invoiceDetailsEntity = invoiceDetailsRepository.save(invoiceDetailsEntity);
        return InvoiceDetailsMapper.INSTANCE.entityToDto(invoiceDetailsEntity);
    }

    private void validateInvoice(InvoiceDetails invoiceDetails) {
        if (invoiceDetails.getInvoiceDate() == null) {
            invoiceDetails.setInvoiceDate(LocalDate.now());
        }

        if (invoiceDetails.getDueDate() == null && invoiceDetails.getInvoiceDate() != null) {
            invoiceDetails.setDueDate(invoiceDetails.getInvoiceDate().plusDays(30));
        }

        if (invoiceDetails.getAmount() == null || invoiceDetails.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invoice amount must be positive");
        }
    }

    private void calculateTaxes(InvoiceDetails invoiceDetails) {
        if (invoiceDetails.getGstRate() != null && invoiceDetails.getGstRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxableAmount = invoiceDetails.getAmount();
            BigDecimal taxAmount = taxableAmount.multiply(invoiceDetails.getGstRate()).divide(new BigDecimal(100));

            if ("INTER".equals(invoiceDetails.getGstStatus())) {
                invoiceDetails.setIgst(taxAmount);
                invoiceDetails.setCgst(BigDecimal.ZERO);
                invoiceDetails.setSgst(BigDecimal.ZERO);
            } else {
                invoiceDetails.setIgst(BigDecimal.ZERO);
                invoiceDetails.setCgst(taxAmount.divide(new BigDecimal(2)));
                invoiceDetails.setSgst(taxAmount.divide(new BigDecimal(2)));
            }
        }
    }

    private void generateInvoiceNumber(InvoiceDetails invoiceDetails) {
        if (invoiceDetails.getInvoiceNumber() == null) {
            String prefix = "SARASVAN-INV";
            String timestamp = String.valueOf(System.currentTimeMillis());
            invoiceDetails.setInvoiceNumber(prefix + "-" + timestamp.substring(5));
        }
    }


    public List<InvoiceDetails> list() {
        List<InvoiceDetailsEntity> invoiceDetailsEntities = invoiceDetailsRepository.findByDeletedFalse();
        return InvoiceDetailsMapper.INSTANCE.entityListToDtoList(invoiceDetailsEntities);
    }

    public Optional<InvoiceDetails> get(Long id) {
        Optional<InvoiceDetailsEntity> invoiceDetailsEntity = invoiceDetailsRepository.findById(id).filter(inv -> !inv.getDeleted());
        return null;
    }

    public void delete(Long id) {
        invoiceDetailsRepository.findById(id).ifPresent(inv -> {
            inv.setDeleted(true);
            invoiceDetailsRepository.save(inv);
        });
    }

    public ByteArrayInputStream exportCsv() {
        List<InvoiceDetails> invoiceDetails = list();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("Invoice Number,Billed To,Currency,Amount,Status,Place Of Supply,Invoice Date,Due Date,Due Amount");
        for (InvoiceDetails inv : invoiceDetails) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    inv.getInvoiceNumber(), inv.getBilledTo(), inv.getCurrency(), inv.getAmount(),
                    inv.getStatus(), inv.getPlaceOfSupply(), inv.getInvoiceDate(),
                    inv.getDueDate(), inv.getDueAmount());
        }
        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }


    public ByteArrayInputStream exportPdf() throws IOException {
        List<InvoiceDetails> invoiceDetails = list();
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

        for (InvoiceDetails inv : invoiceDetails) {
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
        InvoiceDetails invoiceDetails = get(invoiceId)
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
        detailsTable.addCell(createDetailCell(invoiceDetails.getInvoiceNumber(), regularFont));

        detailsTable.addCell(createDetailCell("Invoice Date:", boldFont));
        detailsTable.addCell(createDetailCell(
                invoiceDetails.getInvoiceDate().format(dateFormatter), regularFont));

        detailsTable.addCell(createDetailCell("Due Date:", boldFont));
        detailsTable.addCell(createDetailCell(
                invoiceDetails.getDueDate() != null ? invoiceDetails.getDueDate().format(dateFormatter) : "N/A",
                regularFont));

        detailsTable.addCell(createDetailCell("Billed To:", boldFont));
        detailsTable.addCell(new Cell(1, 2).add(new Paragraph(invoiceDetails.getBilledTo())).setFont(regularFont));

        Table amountTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
        amountTable.setWidth(UnitValue.createPercentValue(50));
        amountTable.setMarginTop(20);

        amountTable.addCell(createDetailCell("Subtotal:", boldFont));
        amountTable.addCell(createDetailCell(invoiceDetails.getAmount().toString(), regularFont));

        if (invoiceDetails.getIgst() != null && invoiceDetails.getIgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("IGST (" + invoiceDetails.getGstRate() + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoiceDetails.getIgst().toString(), regularFont));
        }

        if (invoiceDetails.getCgst() != null && invoiceDetails.getCgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("CGST (" + invoiceDetails.getGstRate().divide(BigDecimal.valueOf(2), RoundingMode.CEILING) + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoiceDetails.getCgst().toString(), regularFont));
        }

        if (invoiceDetails.getSgst() != null && invoiceDetails.getSgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("SGST (" + invoiceDetails.getGstRate().divide(BigDecimal.valueOf(2), RoundingMode.CEILING) + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoiceDetails.getSgst().toString(), regularFont));
        }

        amountTable.addCell(createDetailCell("Total:", boldFont).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        BigDecimal total = invoiceDetails.getAmount()
                .add(invoiceDetails.getIgst() != null ? invoiceDetails.getIgst() : BigDecimal.ZERO)
                .add(invoiceDetails.getCgst() != null ? invoiceDetails.getCgst() : BigDecimal.ZERO)
                .add(invoiceDetails.getSgst() != null ? invoiceDetails.getSgst() : BigDecimal.ZERO);
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