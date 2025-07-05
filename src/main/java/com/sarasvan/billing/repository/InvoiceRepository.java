package com.sarasvan.billing.repository;

import com.sarasvan.billing.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByDeletedFalse();
}
