package sarasvan.billing.main.service;

import sarasvan.billing.main.entity.BusinessDetailsEntity;
import sarasvan.billing.main.mapper.BusinessDetailsMapper;
import sarasvan.billing.main.model.BusinessDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarasvan.billing.main.repository.BusinessDetailsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessDetailsService {

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
        BusinessDetailsEntity businessDetails = BusinessDetailsMapper.INSTANCE.dtoToEntity(details);
        repository.save(businessDetails);
        return null;
    }

    public Optional<BusinessDetails> getById(final Long id) {
        repository.findById(id).stream().findFirst();
        return null;
    }

    private boolean isValidGSTIN(String gstin) {
        return gstin != null && gstin.matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}[A-Z\\d]{1}");
    }
}