package com.sarasvan.billing.enums;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public enum TaxTreatment implements IDEnum<Integer> {
    REGISTERED_B2B(1, "Registered B2B"),
    UN_REGISTERED_B2B(2, "Unregistered B2B"),
    SPECIAL_ECONOMY_ZONE(3, "Special Economy Zone"),
    TAX_DEDUCTOR(4, "Tax Deductor"),
    DEEMED_EXPORT(5, "Deemed Exports");

    private int id;
    private String taxType;

    TaxTreatment(int id, String taxType) {
        this.id = id;
        this.taxType = taxType;
    }

    public Integer get() {
        return id;
    }

    public String getTaxType() {
        return taxType;
    }

    public static TaxTreatment fromValue(Serializable value) {
        return Stream.of(values())
                .filter(v -> Objects.equals(v.get(), value) || equalsIgnoreCase(v.getTaxType(), String.valueOf(value))).findFirst().orElse(null);
    }


}
