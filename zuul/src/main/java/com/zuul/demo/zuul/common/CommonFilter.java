package com.zuul.demo.zuul.common;

import com.zuul.demo.zuul.filter.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author whh
 */
@Component
public class CommonFilter {

    @Bean
    public TokenFilter tokenFilter() {
        return new TokenFilter();
    }

}
