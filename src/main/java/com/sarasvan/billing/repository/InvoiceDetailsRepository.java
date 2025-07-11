package com.sarasvan.billing.repository;

import com.sarasvan.billing.entity.InvoiceDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetailsEntity, Long> {
    List<InvoiceDetailsEntity> findByDeletedFalse();

}
