package com.sarasvan.billing.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum AuthProvider {
    LOCAL(1, "local"),
    GOOGLE(2, "google");

    private int id;
    private String authProvider;

    AuthProvider(int id, String authProvider) {
        this.id = id;
        this.authProvider = authProvider;
    }
}
