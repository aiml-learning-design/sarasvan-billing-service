package sarasvan.billing.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetails {

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