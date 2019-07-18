package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.exception.BadCredentialsException;
import com.darakay.testapp.testapp.exception.BadRequestException;
import com.darakay.testapp.testapp.exception.InvalidAuthorizationHeader;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.security.SecurityTokens;
import com.darakay.testapp.testapp.security.UserData;
import com.darakay.testapp.testapp.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService au;

    public AuthenticationController(AuthenticationService au) {
        this.au = au;
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response)
            throws InvalidAuthorizationHeader, BadCredentialsException {

        String credentials = request.getHeader("Authorization");
        SecurityTokens tokens = au.login(credentials);
        response.setHeader("XXX-AccessToken", tokens.getAccessToken());
        response.setHeader("XXX-RefreshToken", tokens.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logup")
    public ResponseEntity<?> logup(@RequestBody UserCreateRequest request) throws BadRequestException {
        au.logup(request);
        return ResponseEntity.created(URI.create("/auth/login")).build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            HttpServletRequest request, HttpServletResponse response,
            Principal principal)
            throws BadCredentialsException, UserNotFoundException {
        String old = request.getHeader("XXX-RefreshToken");
        UserData userData = (UserData) ((Authentication)principal).getCredentials();
        SecurityTokens tokens = au.refreshTokens(old, userData.getId());
        if(tokens.isSuccess()){
            response.setHeader("XXX-AccessToken", tokens.getAccessToken());
            response.setHeader("XXX-RefreshToken", tokens.getRefreshToken());
            return ResponseEntity.ok().build();
        }
        throw new BadCredentialsException();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) throws UserNotFoundException {
        UserData userData = (UserData) ((Authentication)principal).getCredentials();
        au.logout(userData.getId());
        return ResponseEntity.ok().build();
    }
}
