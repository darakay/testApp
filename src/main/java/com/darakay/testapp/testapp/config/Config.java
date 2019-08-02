package com.darakay.testapp.testapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class Config  {
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    public Config(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @PostConstruct
    public void prioritizeArgumentHandlers(){
        List<HandlerMethodArgumentResolver> springResolvers =
                requestMappingHandlerAdapter.getArgumentResolvers();
        List<HandlerMethodArgumentResolver> customResolvers =
                requestMappingHandlerAdapter.getCustomArgumentResolvers();

        Assert.notNull(springResolvers, "Spring argument resolvers are not configured");
        Assert.notNull(customResolvers, "Custom argument resolvers are not configured");

        //customResolvers.addAll(springResolvers);

        requestMappingHandlerAdapter.setArgumentResolvers(customResolvers);
    }
}
