package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.BadCredentialsException;
import com.darakay.testapp.testapp.exception.BadRequestException;
import com.darakay.testapp.testapp.exception.InvalidAuthorizationHeader;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.security.SecurityTokens;
import com.darakay.testapp.testapp.security.jwt.JwtTokenService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
public class AuthenticationService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthenticationService(JwtTokenService jwtTokenService, UserRepository userRepository, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public SecurityTokens login(String authenticationHeader) throws InvalidAuthorizationHeader, BadCredentialsException {
        String[] credentials = getCredentialsValue(authenticationHeader);
        User user = userRepository.findByLogin(credentials[0]).orElseThrow(BadCredentialsException::new);
        if(user.getPassword().equals(credentials[1])){
            return createTokens(user);
        }
        throw new BadCredentialsException();
    }

    public void logup(UserCreateRequest request) throws  BadRequestException {
        if(userRepository.existsByLogin(request.getLogin()))
            throw new BadRequestException();
        userRepository.save(
                new User(request.getFirstName(), request.getLastName(), request.getLogin(), request.getPassword()));
    }

    public SecurityTokens refreshTokens(String oldRefreshToken, long uid) throws UserNotFoundException {
        User principal = userService.getUserById(uid);
        if(principal.getRefreshToken().equals(oldRefreshToken))
            return createTokens(principal);
        return createFailSecurityTokens();
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

    private SecurityTokens createTokens(User user){
        long expires = createExpiresForUser();
        String accessToken = jwtTokenService.createAccessToken(user.getId(), expires);
        String refreshToken = jwtTokenService.createRefreshToken();
        userRepository.save(user.setSecurityTokens(refreshToken, expires));
        return new SecurityTokens(accessToken, refreshToken);
    }

    private SecurityTokens createFailSecurityTokens(){
        return new SecurityTokens(null, null);
    }

    private long createExpiresForUser(){
        return DateUtils.addMinutes(new Date(), 30).getTime();
    }

    @PreAuthorize(value = "@accountAccessEvaluator.accessTokenIsValid(principal.id)")
    public void logout(long uid) throws UserNotFoundException {
        userService.expireCurrentPrincipal(uid);
    }
}
