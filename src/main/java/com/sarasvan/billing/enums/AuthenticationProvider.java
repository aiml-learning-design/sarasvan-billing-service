package com.sarasvan.billing.enums;

import lombok.Getter;

@Getter
public enum AuthenticationProvider {
    LOCAL(1, "local"),
    GOOGLE(2, "google");

    private int id;
    private String authProvider;

    AuthenticationProvider(int id, String authProvider) {
        this.id = id;
        this.authProvider = authProvider;
    }
}
