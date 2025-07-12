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
import com.sarasvan.billing.entity.InvoiceDetailsEntity;
import com.sarasvan.billing.mapper.InvoiceDetailsMapper;
import com.sarasvan.billing.model.InvoiceDetailsDTO;
import com.sarasvan.billing.repository.InvoiceDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceDetailsRepository invoiceDetailsRepository;

    public InvoiceDetailsDTO create(final InvoiceDetailsDTO invoiceDetailsDTO) {
        validateInvoice(invoiceDetailsDTO);
        generateInvoiceNumber(invoiceDetailsDTO);
        calculateTaxes(invoiceDetailsDTO);
        InvoiceDetailsEntity invoiceDetailsEntity =  InvoiceDetailsMapper.INSTANCE.dtoToEntity(invoiceDetailsDTO);
        invoiceDetailsEntity = invoiceDetailsRepository.save(invoiceDetailsEntity);
        return InvoiceDetailsMapper.INSTANCE.entityToDto(invoiceDetailsEntity);
    }

    public InvoiceDetailsDTO update(Long id, InvoiceDetailsDTO updatedDTO) {
        InvoiceDetailsEntity existing = invoiceDetailsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        updatedDTO.setId(id);
        validateInvoice(updatedDTO);
        calculateTaxes(updatedDTO);
        InvoiceDetailsEntity updated = InvoiceDetailsMapper.INSTANCE.dtoToEntity(updatedDTO);
        updated.setDeleted(existing.getDeleted());
        return InvoiceDetailsMapper.INSTANCE.entityToDto(invoiceDetailsRepository.save(updated));
    }

    public void markAsPaid(Long id) {
        invoiceDetailsRepository.findById(id).ifPresent(inv -> {
            inv.setStatus("PAID");
            inv.setDueAmount(BigDecimal.ZERO);
            invoiceDetailsRepository.save(inv);
        });
    }

    public List<InvoiceDetailsDTO> search(String status, String billedTo, LocalDate startDate, LocalDate endDate) {
        return invoiceDetailsRepository.findAll().stream()
                .filter(inv -> !inv.getDeleted())
                .filter(inv -> status == null || status.equalsIgnoreCase(inv.getStatus()))
                .filter(inv -> billedTo == null || billedTo.equalsIgnoreCase(inv.getBilledTo()))
                .filter(inv -> startDate == null || !inv.getInvoiceDate().isBefore(startDate))
                .filter(inv -> endDate == null || !inv.getInvoiceDate().isAfter(endDate))
                .map(InvoiceDetailsMapper.INSTANCE::entityToDto)
                .collect(Collectors.toList());
    }

    public void restore(Long id) {
        invoiceDetailsRepository.findById(id).ifPresent(inv -> {
            inv.setDeleted(false);
            invoiceDetailsRepository.save(inv);
        });
    }

    private void validateInvoice(InvoiceDetailsDTO invoiceDetailsDTO) {
        if (invoiceDetailsDTO.getInvoiceDate() == null) {
            invoiceDetailsDTO.setInvoiceDate(LocalDate.now());
        }

        if (invoiceDetailsDTO.getDueDate() == null && invoiceDetailsDTO.getInvoiceDate() != null) {
            invoiceDetailsDTO.setDueDate(invoiceDetailsDTO.getInvoiceDate().plusDays(30));
        }

        if (invoiceDetailsDTO.getAmount() == null || invoiceDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invoice amount must be positive");
        }
    }

    private void calculateTaxes(InvoiceDetailsDTO invoiceDetailsDTO) {
        if (invoiceDetailsDTO.getGstRate() != null && invoiceDetailsDTO.getGstRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxableAmount = invoiceDetailsDTO.getAmount();
            BigDecimal taxAmount = taxableAmount.multiply(invoiceDetailsDTO.getGstRate()).divide(new BigDecimal(100));

            if ("INTER".equalsIgnoreCase(invoiceDetailsDTO.getGstStatus())) {
                invoiceDetailsDTO.setIgst(taxAmount);
                invoiceDetailsDTO.setCgst(BigDecimal.ZERO);
                invoiceDetailsDTO.setSgst(BigDecimal.ZERO);
            } else {
                BigDecimal half = taxAmount.divide(new BigDecimal(2), RoundingMode.HALF_UP);
                invoiceDetailsDTO.setIgst(BigDecimal.ZERO);
                invoiceDetailsDTO.setCgst(half);
                invoiceDetailsDTO.setSgst(half);
            }
        }
    }

    private void generateInvoiceNumber(InvoiceDetailsDTO invoiceDetailsDTO) {
        if (invoiceDetailsDTO.getInvoiceNumber() == null) {
            String prefix = "SARASVAN-INV";
            String timestamp = String.valueOf(System.currentTimeMillis());
            invoiceDetailsDTO.setInvoiceNumber(prefix + "-" + timestamp.substring(5));
        }
    }


    public List<InvoiceDetailsDTO> list() {
        List<InvoiceDetailsEntity> invoiceDetailsEntities = invoiceDetailsRepository.findByDeletedFalse();
        return InvoiceDetailsMapper.INSTANCE.entityListToDtoList(invoiceDetailsEntities);
    }
    public Optional<InvoiceDetailsDTO> get(Long id) {
        return invoiceDetailsRepository.findById(id)
                .filter(inv -> !inv.getDeleted())
                .map(InvoiceDetailsMapper.INSTANCE::entityToDto);
    }

    public Page<InvoiceDetailsDTO> getInvoicesByBusinessId(Long businessId, String status, LocalDate from, LocalDate to, Pageable pageable) {
        Page<InvoiceDetailsEntity> entityPage = invoiceDetailsRepository.findByBusinessIdFiltered(
                businessId, status, from, to, pageable);
        return entityPage.map(InvoiceDetailsMapper.INSTANCE::entityToDto);
    }

    public void delete(Long id) {
        invoiceDetailsRepository.findById(id).ifPresent(inv -> {
            inv.setDeleted(true);
            invoiceDetailsRepository.save(inv);
        });
    }

    public ByteArrayInputStream exportCsv() {
        List<InvoiceDetailsDTO> invoiceDetailDTOS = list();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("Invoice Number,Billed To,Currency,Amount,Status,Place Of Supply,Invoice Date,Due Date,Due Amount");
        for (InvoiceDetailsDTO inv : invoiceDetailDTOS) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    inv.getInvoiceNumber(), inv.getBilledTo(), inv.getCurrency(), inv.getAmount(),
                    inv.getStatus(), inv.getPlaceOfSupply(), inv.getInvoiceDate(),
                    inv.getDueDate(), inv.getDueAmount());
        }
        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }


    public ByteArrayInputStream exportPdf() throws IOException {
        List<InvoiceDetailsDTO> invoiceDetailDTOS = list();
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

        for (InvoiceDetailsDTO inv : invoiceDetailDTOS) {
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

    public ByteArrayInputStream exportInvoiceAsPdf(Long invoiceId) throws IOException {
        InvoiceDetailsDTO invoiceDetailsDTO = get(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph title = new Paragraph("INVOICE")
                .setFont(titleFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        float[] columnWidths = {3, 1, 3};
        Table detailsTable = new Table(UnitValue.createPercentArray(columnWidths));
        detailsTable.setWidth(UnitValue.createPercentValue(80));
        detailsTable.setMarginBottom(20);

        detailsTable.addCell(createDetailCell("Invoice Number:", boldFont));
        detailsTable.addCell(createDetailCell(invoiceDetailsDTO.getInvoiceNumber(), regularFont));

        detailsTable.addCell(createDetailCell("Invoice Date:", boldFont));
        detailsTable.addCell(createDetailCell(
                invoiceDetailsDTO.getInvoiceDate().format(dateFormatter), regularFont));

        detailsTable.addCell(createDetailCell("Due Date:", boldFont));
        detailsTable.addCell(createDetailCell(
                invoiceDetailsDTO.getDueDate() != null ? invoiceDetailsDTO.getDueDate().format(dateFormatter) : "N/A",
                regularFont));

        detailsTable.addCell(createDetailCell("Billed To:", boldFont));
        detailsTable.addCell(new Cell(1, 2).add(new Paragraph(invoiceDetailsDTO.getBilledTo())).setFont(regularFont));

        Table amountTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
        amountTable.setWidth(UnitValue.createPercentValue(50));
        amountTable.setMarginTop(20);

        amountTable.addCell(createDetailCell("Subtotal:", boldFont));
        amountTable.addCell(createDetailCell(invoiceDetailsDTO.getAmount().toString(), regularFont));

        if (invoiceDetailsDTO.getIgst() != null && invoiceDetailsDTO.getIgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("IGST (" + invoiceDetailsDTO.getGstRate() + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoiceDetailsDTO.getIgst().toString(), regularFont));
        }

        if (invoiceDetailsDTO.getCgst() != null && invoiceDetailsDTO.getCgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("CGST (" + invoiceDetailsDTO.getGstRate().divide(BigDecimal.valueOf(2), RoundingMode.CEILING) + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoiceDetailsDTO.getCgst().toString(), regularFont));
        }

        if (invoiceDetailsDTO.getSgst() != null && invoiceDetailsDTO.getSgst().compareTo(BigDecimal.ZERO) > 0) {
            amountTable.addCell(createDetailCell("SGST (" + invoiceDetailsDTO.getGstRate().divide(BigDecimal.valueOf(2), RoundingMode.CEILING) + "%):", boldFont));
            amountTable.addCell(createDetailCell(invoiceDetailsDTO.getSgst().toString(), regularFont));
        }

        amountTable.addCell(createDetailCell("Total:", boldFont).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        BigDecimal total = invoiceDetailsDTO.getAmount()
                .add(invoiceDetailsDTO.getIgst() != null ? invoiceDetailsDTO.getIgst() : BigDecimal.ZERO)
                .add(invoiceDetailsDTO.getCgst() != null ? invoiceDetailsDTO.getCgst() : BigDecimal.ZERO)
                .add(invoiceDetailsDTO.getSgst() != null ? invoiceDetailsDTO.getSgst() : BigDecimal.ZERO);
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