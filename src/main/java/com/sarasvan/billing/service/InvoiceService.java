package com.sarasvan.billing.service;

import com.sarasvan.billing.model.InvoiceDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    InvoiceDetailsDTO create(InvoiceDetailsDTO invoiceDetailsDTO);
    InvoiceDetailsDTO update(Long id, InvoiceDetailsDTO updatedDTO);
    List<InvoiceDetailsDTO> list();
    Optional<InvoiceDetailsDTO> get(Long id);
    Page<InvoiceDetailsDTO> getInvoicesByBusinessId(Long businessId, String status, LocalDate from, LocalDate to, Pageable pageable);
    void delete(Long id);
    void restore(Long id);
    void markAsPaid(Long id);
    List<InvoiceDetailsDTO> search(String status, Long billedToBusinessId, LocalDate from, LocalDate to);
    ByteArrayInputStream exportCsv();
    ByteArrayInputStream exportPdf() throws IOException;
    ByteArrayInputStream exportInvoiceAsPdf(Long invoiceId) throws IOException;
}
