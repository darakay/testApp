package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.BadCredentialsException;
import com.darakay.testapp.testapp.exception.InvalidAuthorizationHeader;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.security.jwt.JwtTokenService;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthenticationService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public AuthenticationService(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    public String login(String authenticationHeader) throws InvalidAuthorizationHeader, BadCredentialsException {
        String[] credentials = getCredentialsValue(authenticationHeader);
        User user = userRepository.findByLogin(credentials[0]).orElseThrow(BadCredentialsException::new);
        if(user.getPassword().equals(credentials[1])){
            return jwtTokenService.create(user.getId());
        }
        throw new BadCredentialsException();
    }

    private String[] getCredentialsValue(String headerValue) throws InvalidAuthorizationHeader {
        if(headerValue == null ||headerValue.split(" ").length != 2)
            throw new InvalidAuthorizationHeader();
        String encodedValue = headerValue.split(" ")[1];
        String decodedValue = new String(Base64.getDecoder().decode(encodedValue));
        if(decodedValue.split(":").length != 2)
            throw new InvalidAuthorizationHeader();
        return decodedValue.split(":");
    }
}
