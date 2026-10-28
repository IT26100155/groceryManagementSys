package com.grocerymanagementsys.common.config;

import com.grocerymanagementsys.inventory.services.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Serves uploaded item pictures from the upload folder at /uploads/**. */
@Configuration
@RequiredArgsConstructor
public class UploadsConfig implements WebMvcConfigurer {
    private final ImageStorageService imageStorage;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = imageStorage.getUploadRoot().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
