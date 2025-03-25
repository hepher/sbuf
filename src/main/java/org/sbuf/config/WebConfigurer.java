package org.sbuf.config;

import org.sbuf.resolver.*;
import org.sbuf.security.validation.SecurityRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserIdArgumentResolver());
        argumentResolvers.add(new JwtArgumentResolver());
        argumentResolvers.add(new UserIpArgumentResolver());
        argumentResolvers.add(new TransactionIdArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityRequestInterceptor()).addPathPatterns("/**");
    }
}
