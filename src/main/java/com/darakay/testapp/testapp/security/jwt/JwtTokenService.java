package com.darakay.testapp.testapp.security.jwt;

public interface JwtTokenService {
    String createAccessToken(long userId, long expires);
    String createRefreshToken();
}
