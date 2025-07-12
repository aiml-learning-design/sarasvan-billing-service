package com.sarasvan.billing.util;

import java.util.Set;

public class GSTINValidator {

    private static final Set<String> VALID_STATE_CODES = Set.of(
            "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37"
    );

    private static final String GSTIN_REGEX = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}[A-Z\\d]{1}";

    /**
     * Validates a GSTIN for correct format and state code.
     *
     * @param gstin GSTIN to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidGSTIN(String gstin) {
        if (gstin == null || !gstin.matches(GSTIN_REGEX)) {
            return false;
        }

        String stateCode = gstin.substring(0, 2);
        return VALID_STATE_CODES.contains(stateCode);
    }
}
