package com.sarasvan.billing.controller;

import com.sarasvan.billing.model.InvoiceDetailsDTO;
import com.sarasvan.billing.service.InvoiceService;
import com.sarasvan.billing.service.InvoiceServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDetailsDTO> createInvoice(@Valid @RequestBody InvoiceDetailsDTO invoiceDetailsDTO) {
        return ResponseEntity.ok(invoiceService.create(invoiceDetailsDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDetailsDTO> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceDetailsDTO invoiceDetailsDTO) {
        return ResponseEntity.ok(invoiceService.update(id, invoiceDetailsDTO));
    }
    @GetMapping
    public ResponseEntity<List<InvoiceDetailsDTO>> listInvoices() {
        return ResponseEntity.ok(invoiceService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetailsDTO> getInvoice(@PathVariable Long id) {
        return invoiceService.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/restore/{id}")
    public ResponseEntity<Void> restoreInvoice(@PathVariable Long id) {
        invoiceService.restore(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/business/{businessId}/invoices")
    public ResponseEntity<Page<InvoiceDetailsDTO>> getInvoicesByBusinessId(
            @PathVariable Long businessId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("invoiceDate").descending());
        Page<InvoiceDetailsDTO> result = invoiceService.getInvoicesByBusinessId(businessId, status, from, to, pageable);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<Void> markInvoicePaid(@PathVariable Long id) {
        invoiceService.markAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<InvoiceDetailsDTO>> searchInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String billedTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.search(status, billedTo, startDate, endDate));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportToCsv() {
        ByteArrayInputStream stream = invoiceService.exportCsv();
        InputStreamResource resource = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoices.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportToPdf() {
        try {
            InputStreamResource resource = new InputStreamResource(invoiceService.exportPdf());
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
            InputStreamResource resource = new InputStreamResource(invoiceService.exportInvoiceAsPdf(id));
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
            InputStreamResource resource = new InputStreamResource(invoiceService.exportInvoiceAsPdf(id));
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
