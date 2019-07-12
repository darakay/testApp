package com.darakay.testapp.testapp.security;


import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.repos.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppAuthenticationProvider  implements AuthenticationProvider {

    private final UserRepository userRepository;

    public AppAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String[] names = authentication.getName().split(" ");
        User user = userRepository.findByFirstNameAndLastName(names[0], names[1]);
        if(user == null)
            throw new UsernameNotFoundException("User name not found!");
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(authentication.getCredentials().toString().equals(user.getPassword()))
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        throw new BadCredentialsException("Invalid user name or password");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
