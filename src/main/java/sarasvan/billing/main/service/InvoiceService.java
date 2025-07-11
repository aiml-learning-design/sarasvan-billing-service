package sarasvan.billing.main.service;

import sarasvan.billing.main.model.InvoiceDetails;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    InvoiceDetails create(InvoiceDetails invoiceDetails);
    List<InvoiceDetails> list();
    Optional<InvoiceDetails> get(Long id);
    void delete(Long id);
    ByteArrayInputStream exportCsv();
    ByteArrayInputStream exportPdf() throws IOException;
    ByteArrayInputStream exportInvoiceAsPdf(Long invoiceId) throws IOException;
}
