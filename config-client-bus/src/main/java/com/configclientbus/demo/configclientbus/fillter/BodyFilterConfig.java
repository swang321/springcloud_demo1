package com.configclientbus.demo.configclientbus.fillter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author whh
 */
@Configuration
public class BodyFilterConfig {

    @Bean
    public FilterRegistrationBean regBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new BodyFilter());
        registration.addUrlPatterns("/actuator/refresh");
        registration.setName("RequestBodyFilter");
        registration.setOrder(1);
        return registration;
    }
}
