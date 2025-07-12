package com.sarasvan.billing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sarasvan.billing.enums.IndianStateOrUT;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="office_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressLine;

    @Column(name = "city", nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private IndianStateOrUT state;

    private String pincode;

    private String officePhone;

    @Builder.Default
    @Column(name = "country", nullable = false)
    private String country = "India";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnore
    private BusinessDetailsEntity business;
}