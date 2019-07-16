package com.darakay.testapp.testapp.config;

import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.loadByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));
        return new UserData(user.getId(), user.getPassword(), user.getLogin());
    }
}
