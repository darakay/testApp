package com.darakay.testapp.testapp.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service(value = "authenticationProvider")
public class CustomAuthProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserData userdata = (UserData) authentication.getPrincipal();
        if(userdata.getPassword().equals(authentication.getCredentials().toString()))
            return new UsernamePasswordAuthenticationToken(userdata, authentication.getCredentials());
        throw  new BadCredentialsException("Bad credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
