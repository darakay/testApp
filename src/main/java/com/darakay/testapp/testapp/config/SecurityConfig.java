package com.darakay.testapp.testapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private  CustomUserDetailsService userDetailsService;
    private  CustomAuthProvider authProvider;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomAuthProvider authProvider) {
        super();
        this.userDetailsService = userDetailsService;
        this.authProvider = authProvider;
    }

    public SecurityConfig(boolean disableDefaults, CustomUserDetailsService userDetailsService, CustomAuthProvider authProvider) {
        super(disableDefaults);
        this.userDetailsService = userDetailsService;
        this.authProvider = authProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .userDetailsService(userDetailsService).authenticationProvider(authProvider)
                .addFilterBefore(headerAuthFilter(), RequestHeaderAuthenticationFilter.class)
                .authorizeRequests().antMatchers("/accounts/**").authenticated();
    }

    @Bean
    protected Filter headerAuthFilter() throws Exception {
        final RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setExceptionIfHeaderMissing(true);
        filter.setPrincipalRequestHeader("Authorisation");
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }


}
