package com.sarasvan.billing.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDetails {

    private Long id;
    private String businessName;
    private String address;
    private String gstin;
    private String pan;
    private String email;
    private String phone;
}
