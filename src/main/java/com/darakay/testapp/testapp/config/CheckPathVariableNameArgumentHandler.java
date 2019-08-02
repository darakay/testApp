package com.darakay.testapp.testapp.config;

import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CheckPathVariableNameArgumentHandler implements HandlerMethodArgumentResolver {

    @Value("{app.urlPrefix}")
    private String urlPrefix;

    @Value("{app.expectingName}")
    private String expectingName;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Method method = parameter.getMethod();
        AnnotatedElement ae =parameter.getAnnotatedElement();
        String name = method.getName();
        return parameter;
    }

    private String[] getEndpointUrl(Method method){

       return Arrays.stream(method.getDeclaredAnnotations())
               .filter(a -> isRequestMappingAnnotation(a.annotationType()))
               .findFirst()
               .map(a -> a.annotationType().getAnnotation(RequestMapping.class).value()).get();


    }

    private boolean isRequestMappingAnnotation(Class annotationClass){
        return Arrays.stream(annotationClass.getAnnotations()).anyMatch(
                a -> a.annotationType().equals(RequestMapping.class));
    }


}
