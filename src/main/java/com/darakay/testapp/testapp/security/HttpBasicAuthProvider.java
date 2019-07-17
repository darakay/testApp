package com.darakay.testapp.testapp.security;

import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HttpBasicAuthProvider implements AuthenticationProvider {

    private final UserService userService;

    public HttpBasicAuthProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        User user = userService.loadByLogin(login).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
        if(user.getPassword().equals(authentication.getCredentials().toString()))
            return new AuthenticatedUserToken(
                new UserData(user.getId(), user.getPassword(), user.getLogin()));
        throw new BadCredentialsException("invalid login or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
