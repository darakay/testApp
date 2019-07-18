package com.darakay.testapp.testapp.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    @Override
    public String createAccessToken(long userId, long expires) {
        return Jwts.builder()
                .claim("id", userId)
                .claim("expiresAt", expires)
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();
    }

    @Override
    public String createRefreshToken() {
        return Long.toString(new Random().nextLong());
    }
}
