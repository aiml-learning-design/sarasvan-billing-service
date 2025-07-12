package com.sarasvan.billing.repository;

import com.sarasvan.billing.entity.InvoiceDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetailsEntity, Long> {
    List<InvoiceDetailsEntity> findByDeletedFalse();

    @Query("""
    SELECT i FROM InvoiceDetailsEntity i
    WHERE i.businessId = :businessId
      AND (:status IS NULL OR i.status = :status)
      AND (:from IS NULL OR i.invoiceDate >= :from)
      AND (:to IS NULL OR i.invoiceDate <= :to)
      AND i.deleted = false
""")
    Page<InvoiceDetailsEntity> findByBusinessIdFiltered(
            @Param("businessId") Long businessId,
            @Param("status") String status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable);
}
