package com.sarasvan.billing.service;

import com.sarasvan.billing.model.BusinessDetails;
import com.sarasvan.billing.repository.BusinessDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessDetailsService {

    private final BusinessDetailsRepository repository;

    public BusinessDetails createOrUpdate(BusinessDetails details) {
        if (repository.count() > 0) {
            BusinessDetails existing = repository.findAll().get(0);
            details.setId(existing.getId());
        }
        return repository.save(details);
    }

    public Optional<BusinessDetails> getById(final Long id) {
        return repository.findById(id).stream().findFirst();
    }
}