package sarasvan.billing.main.service;

import sarasvan.billing.main.model.BusinessDetails;

import java.util.Optional;

public interface BusinessDetailsService {

    /**
     * Creates or updates business details
     * @param details Business details to create/update
     * @return The saved business details
     * @throws IllegalArgumentException if GSTIN is invalid
     */
    BusinessDetails createOrUpdate(BusinessDetails details);

    /**
     * Retrieves business details by ID
     * @param id The business details ID
     * @return Optional containing business details if found
     */
    Optional<BusinessDetails> getById(Long id);

    /**
     * Validates GSTIN format
     * @param gstin The GSTIN to validate
     * @return true if valid, false otherwise
     */
    boolean isValidGSTIN(String gstin);
}
