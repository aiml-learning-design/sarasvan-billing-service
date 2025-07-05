package com.sarasvan.billing.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String invoiceNumber;
    private String billedTo;
    private String currency;
    private BigDecimal amount;
    private String status;
    private String placeOfSupply;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal dueAmount;
    private String gstStatus;
    private BigDecimal gstRate;
    private BigDecimal igst;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private Boolean deleted = false;
    private String createdBy;
}