package com.sarasvan.billing.service;

import com.sarasvan.billing.entity.BusinessDetailsEntity;
import com.sarasvan.billing.entity.OfficeAddressEntity;
import com.sarasvan.billing.exception.handler.BusinessException;
import com.sarasvan.billing.mapper.BusinessDetailsMapper;
import com.sarasvan.billing.mapper.OfficeAddressMapper;
import com.sarasvan.billing.model.BusinessDetailsDTO;
import com.sarasvan.billing.model.OfficeAddressDTO;
import com.sarasvan.billing.repository.BusinessDetailsRepository;
import com.sarasvan.billing.util.GSTINValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessDetailsServiceImpl implements BusinessDetailsService {

    private final BusinessDetailsRepository repository;

    @Override
    @Transactional
    public BusinessDetailsDTO createOrUpdate(@Valid @NotNull final BusinessDetailsDTO details) {

        if (details.getGstin() != null && !GSTINValidator.isValidGSTIN(details.getGstin())) {
            throw new BusinessException("Invalid GSTIN format");
        }

        BusinessDetailsEntity entity;
        List<BusinessDetailsEntity> existingList = repository.findAll();

        if (!existingList.isEmpty()) {
            entity = existingList.get(0);
            details.setId(entity.getId());
        } else {
            details.setId(null);
        }

        BusinessDetailsEntity saved = repository.save(
                BusinessDetailsMapper.INSTANCE.dtoToEntity(details)
        );

        return BusinessDetailsMapper.INSTANCE.entityToDto(saved);
    }

    @Override
    @Transactional
    public BusinessDetailsDTO updateOfficeAddress(@Valid @NotNull Long businessId,
                                                  @Valid @NotNull OfficeAddressDTO officeAddressDTO) {

        BusinessDetailsEntity entity = repository.findById(businessId)
                .orElseThrow(() -> new BusinessException("Business not found with ID: " + businessId));

        OfficeAddressEntity newAddress = OfficeAddressMapper.INSTANCE.dtoToEntity(officeAddressDTO);
        entity.addOfficeAddress(newAddress);

        BusinessDetailsEntity updated = repository.save(entity);
        return BusinessDetailsMapper.INSTANCE.entityToDto(updated);
    }

    @Override
    @Transactional
    public BusinessDetailsDTO getById(@Valid @NotNull final Long businessId) {
        BusinessDetailsEntity entity = repository.findById(businessId)
                .orElseThrow(() -> new BusinessException("Business not found with ID: " + businessId));

        return BusinessDetailsMapper.INSTANCE.entityToDto(entity);
    }
}