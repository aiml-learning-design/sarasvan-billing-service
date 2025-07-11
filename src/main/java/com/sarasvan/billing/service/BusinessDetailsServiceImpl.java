package com.sarasvan.billing.service;

import com.sarasvan.billing.entity.BusinessDetailsEntity;
import com.sarasvan.billing.mapper.BusinessDetailsMapper;
import com.sarasvan.billing.model.BusinessDetails;
import com.sarasvan.billing.repository.BusinessDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessDetailsServiceImpl implements BusinessDetailsService {

    private final BusinessDetailsRepository repository;

    public BusinessDetails createOrUpdate(final BusinessDetails details) {
        List<BusinessDetailsEntity> existing = repository.findAll();
        if (!existing.isEmpty()) {
            if (existing.size() > 1) {
                repository.deleteAll();
                details.setId(null);
            } else {
                details.setId(existing.get(0).getId());
            }
        }

        if (details.getGstin() != null && !isValidGSTIN(details.getGstin())) {
            throw new IllegalArgumentException("Invalid GSTIN format");
        }
        repository.save(BusinessDetailsMapper.INSTANCE.dtoToEntity(details));
        return details;
    }
    public BusinessDetails getById(final Long id) {

        Optional<BusinessDetailsEntity> businessDetails = repository.findById(id).stream().findFirst();
        return businessDetails.isPresent() ? BusinessDetailsMapper.INSTANCE.entityToDto(businessDetails.get()) : null;
    }

    private boolean isValidGSTIN(String gstin) {
        return gstin != null && gstin.matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}[A-Z\\d]{1}");
    }
}