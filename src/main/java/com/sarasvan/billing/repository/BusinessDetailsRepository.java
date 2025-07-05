package com.sarasvan.billing.repository;

import com.sarasvan.billing.model.BusinessDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDetailsRepository extends JpaRepository<BusinessDetails, Long> {
}
