package com.grocerymanagementsys.staff.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaffWebConfig implements WebMvcConfigurer {
    private final StaffAuthInterceptor staffAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(staffAuthInterceptor).addPathPatterns("/staff", "/staff/**");
    }
}
