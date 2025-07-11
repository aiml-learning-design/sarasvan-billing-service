package com.sarasvan.billing.service;

import com.sarasvan.billing.model.BusinessDetails;

public interface BusinessDetailsService {
    BusinessDetails createOrUpdate(final BusinessDetails details);
    BusinessDetails getById(final Long id);
}
