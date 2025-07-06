package com.sarasvan.billing.dto;

public record AuthRequest(
        String email,
        String password
) {}
