package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.exception.BadCredentialsException;
import com.darakay.testapp.testapp.exception.BadRequestException;
import com.darakay.testapp.testapp.exception.InvalidAuthorizationHeader;
import com.darakay.testapp.testapp.security.SecurityTokens;
import com.darakay.testapp.testapp.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

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
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws BadCredentialsException {
        String old = request.getHeader("XXX-RefreshToken");
        SecurityTokens tokens = au.refreshTokens(old);
        if(tokens.isSuccess()){
            response.setHeader("XXX-AccessToken", tokens.getAccessToken());
            response.setHeader("XXX-RefreshToken", tokens.getRefreshToken());
            return ResponseEntity.ok().build();
        }
        throw new BadCredentialsException();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(){
        au.logout();
        return ResponseEntity.ok().build();
    }
}
