package com.sarasvan.billing.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    public AuthResponse(String token) {
        this.token = token;
    }

}

