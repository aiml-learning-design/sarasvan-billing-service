package com.sarasvan.billing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name="invoice_details", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, updatable = false)
    private String invoiceNumber;

    @PrePersist
    public void generateInvoiceNumber() {
        if (this.invoiceNumber == null || this.invoiceNumber.isBlank()) {
            String prefix = "SARASVAN-INV";
            String timestamp = String.valueOf(System.currentTimeMillis());
            this.invoiceNumber = prefix + "-" + timestamp.substring(5);
        }
    }

    @Column(nullable = false)
    @NotBlank
    private String billedTo;

    @Column(nullable = false)
    @NotBlank
    private String currency;

    @Column(name = "invoice_value", nullable = false)
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotBlank
    private String status;

    @Column(nullable = false)
    @NotBlank
    private String placeOfSupply;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    private LocalDate dueDate;

    @DecimalMin(value = "0.00")
    private BigDecimal dueAmount;

    private String gstStatus;

    @DecimalMin(value = "0.00")
    private BigDecimal gstRate;

    @DecimalMin(value = "0.00")
    private BigDecimal igst;

    @DecimalMin(value = "0.00")
    private BigDecimal cgst;

    @DecimalMin(value = "0.00")
    private BigDecimal sgst;

    private Boolean deleted = false;

    @NotBlank
    private String createdBy;

    @Column(nullable = false)
    private Long businessId;

}