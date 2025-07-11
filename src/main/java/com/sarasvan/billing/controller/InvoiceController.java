package com.sarasvan.billing.controller;

import com.sarasvan.billing.model.InvoiceDetails;
import com.sarasvan.billing.service.InvoiceServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceServiceImpl invoiceServiceImpl;

    @PostMapping
    public ResponseEntity<InvoiceDetails> createInvoice(@Valid @RequestBody InvoiceDetails invoiceDetails) {
        return ResponseEntity.ok(invoiceServiceImpl.create(invoiceDetails));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDetails>> listInvoices() {
        return ResponseEntity.ok(invoiceServiceImpl.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetails> getInvoice(@PathVariable Long id) {
        return invoiceServiceImpl.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportToCsv() {
        ByteArrayInputStream stream = invoiceServiceImpl.exportCsv();
        InputStreamResource resource = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoices.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportToPdf() {
        try {
            InputStreamResource resource = new InputStreamResource(invoiceServiceImpl.exportPdf());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoices_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<InputStreamResource> exportInvoicePdf(@PathVariable Long id) {
        try {
            InputStreamResource resource = new InputStreamResource(invoiceServiceImpl.exportInvoiceAsPdf(id));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/preview/pdf")
    public ResponseEntity<InputStreamResource> previewInvoicePdf(@PathVariable Long id) {
        try {
            InputStreamResource resource = new InputStreamResource(invoiceServiceImpl.exportInvoiceAsPdf(id));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
