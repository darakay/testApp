package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.exception.BadCredentialsException;
import com.darakay.testapp.testapp.exception.BadRequestException;
import com.darakay.testapp.testapp.exception.InvalidAuthorizationHeader;
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
        String token = au.login(credentials);
        response.setHeader("XXX-JwtToken", token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logup")
    public ResponseEntity<?> logup(@RequestBody UserCreateRequest request) throws BadRequestException {
        return ResponseEntity.created(URI.create("/api/users/"+au.logup(request).getId())).build();
    }
}
