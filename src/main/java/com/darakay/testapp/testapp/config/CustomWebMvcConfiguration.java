package com.darakay.testapp.testapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CustomWebMvcConfiguration implements WebMvcConfigurer {

    private CheckPathVariableNameArgumentHandler checkPathVariableNameArgumentHandler;

    public CustomWebMvcConfiguration(CheckPathVariableNameArgumentHandler checkPathVariableNameArgumentHandler) {
        this.checkPathVariableNameArgumentHandler = checkPathVariableNameArgumentHandler;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(checkPathVariableNameArgumentHandler);
    }
}
