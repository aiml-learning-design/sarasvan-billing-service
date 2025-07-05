package com.sarasvan.billing.service;

import com.sarasvan.billing.model.Invoice;
import com.sarasvan.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository repository;

    public Invoice create(Invoice invoice) {
        return repository.save(invoice);
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
}