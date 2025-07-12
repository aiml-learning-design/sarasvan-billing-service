package com.sarasvan.billing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDetailsDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("billedTo")
    private String billedTo;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("placeOfSupply")
    private String placeOfSupply;

    @JsonProperty("invoiceDate")
    private LocalDate invoiceDate;

    @JsonProperty("dueDate")
    private LocalDate dueDate;

    @JsonProperty("dueAmount")
    private BigDecimal dueAmount;

    @JsonProperty("gstStatus")
    private String gstStatus;

    @JsonProperty("gstRate")
    private BigDecimal gstRate;

    @JsonProperty("igst")
    private BigDecimal igst;

    @JsonProperty("cgst")
    private BigDecimal cgst;

    @JsonProperty("sgst")
    private BigDecimal sgst;

    @JsonProperty("deleted")
    private Boolean deleted = false;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("businessId")
    private Long businessId;
}