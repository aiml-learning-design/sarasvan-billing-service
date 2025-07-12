package com.sarasvan.billing.service;

import com.sarasvan.billing.model.BusinessDetailsDTO;
import com.sarasvan.billing.model.OfficeAddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface BusinessDetailsService {
    BusinessDetailsDTO createOrUpdate(@Valid @NotNull final BusinessDetailsDTO details);

    public BusinessDetailsDTO updateOfficeAddress(
            @Valid @NotNull final Long businessId,
            @Valid @NotNull OfficeAddressDTO officeAddressDTO
    );

    BusinessDetailsDTO getById(@Valid @NotNull final Long id);
}
