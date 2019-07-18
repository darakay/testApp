package com.darakay.testapp.testapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/auth/logup","/auth/login").anonymous()
                .and()
                .authorizeRequests().antMatchers("/auth/refresh","/api/**").authenticated()
                .and()
                .addFilterBefore(filter(), RequestHeaderAuthenticationFilter.class)
                .logout().logoutSuccessUrl("/auth/logout")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
    }

    @Bean
    public RequestHeaderAuthenticationFilter filter() throws Exception {
        final RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setExceptionIfHeaderMissing(false);
        filter.setPrincipalRequestHeader("XXX-AccessToken");
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

}
