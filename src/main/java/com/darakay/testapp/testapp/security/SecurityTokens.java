package com.darakay.testapp.testapp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SecurityTokens {
    private String accessToken;
    private String refreshToken;

    public boolean isSuccess() {
        return accessToken != null;
    }
}
