package com.lox.configserver.config;

import com.lox.configserver.interceptor.ClientRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ClientRequestInterceptor clientRequestInterceptor;

    // Constructor injection for ClientRequestInterceptor
    public WebConfig(ClientRequestInterceptor clientRequestInterceptor) {
        this.clientRequestInterceptor = clientRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clientRequestInterceptor);
    }
}
