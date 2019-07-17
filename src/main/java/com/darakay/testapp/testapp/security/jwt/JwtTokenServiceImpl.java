package com.darakay.testapp.testapp.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    @Override
    public String create(long userId) {
        return Jwts.builder()
                .claim("id", userId)
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();
    }
}
