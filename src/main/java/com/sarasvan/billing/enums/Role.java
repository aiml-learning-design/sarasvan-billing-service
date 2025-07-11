package com.sarasvan.billing.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER(1, "user"),
    ADMIN(2, "admin");

    private int id;
    private String userType;

    Role(int id, String userType) {
        this.id = id;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String getAuthority() {
        return name();
    }

}

