package com.sarasvan.billing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "gstin")
    private String gstin;

    @Column(name = "pan", nullable = false)
    private String pan;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "office_phone")
    private String phone;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficeAddressEntity> officeAddresses;

    public void addOfficeAddress(OfficeAddressEntity address) {
        officeAddresses.add(address);
        address.setBusiness(this);
    }

    public void removeOfficeAddress(OfficeAddressEntity address) {
        officeAddresses.remove(address);
        address.setBusiness(null);
    }
}