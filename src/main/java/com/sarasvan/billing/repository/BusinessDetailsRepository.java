package com.sarasvan.billing.repository;

import com.sarasvan.billing.entity.BusinessDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDetailsRepository extends JpaRepository<BusinessDetailsEntity, Long> {
}
