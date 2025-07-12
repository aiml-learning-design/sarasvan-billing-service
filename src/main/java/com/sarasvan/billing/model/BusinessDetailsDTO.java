package com.sarasvan.billing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessDetailsDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("businessName")
    @NotBlank(message = "Business name is required")
    private String businessName;

    @JsonProperty("officeAddresses")
    @NotNull(message = "Office addresses cannot be null")
    @Size(min = 1, message = "At least one office address is required")
    private List<OfficeAddressDTO> officeAddresses;

    @JsonProperty("gstin")
    private String gstin;

    @JsonProperty("pan")
    @Pattern(regexp = "[A-Z]{5}\\d{4}[A-Z]{1}", message = "Invalid PAN format")
    private String pan;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;
}