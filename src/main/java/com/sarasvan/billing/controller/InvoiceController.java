package com.sarasvan.billing.controller;

import com.sarasvan.billing.model.Invoice;
import com.sarasvan.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @PostMapping
    public Invoice create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @GetMapping
    public List<Invoice> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Invoice get(@PathVariable Long id) {
        return service.get(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] data = service.exportCsv().readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoices.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
