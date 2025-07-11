package com.sarasvan.billing.service;

import com.sarasvan.billing.model.InvoiceDetails;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    public InvoiceDetails create(final InvoiceDetails invoiceDetails);
    public List<InvoiceDetails> list();
    public Optional<InvoiceDetails> get(Long id);
    public void delete(Long id);
    public ByteArrayInputStream exportCsv();
    public ByteArrayInputStream exportPdf() throws IOException;
    public ByteArrayInputStream exportInvoiceAsPdf(Long invoiceId) throws IOException ;
}
