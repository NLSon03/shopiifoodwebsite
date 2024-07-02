package com.group6b.shopiifoodwebsite.ultis;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Thay đổi đường dẫn tuyệt đối phù hợp với cấu trúc thư mục của bạn
        registry.addResourceHandler("/foodimages/**")
                .addResourceLocations("file:src/main/resources/static/foodimages/")
                .setCacheControl(CacheControl.noCache().cachePublic());
        registry.addResourceHandler("/restaurantpictures/**")
                .addResourceLocations("file:src/main/resources/static/restaurantpictures/")
                .setCacheControl(CacheControl.noCache().cachePublic());
        registry.addResourceHandler("/categoryicons/**")
                .addResourceLocations("file:src/main/resources/static/categoryicons/")
                .setCacheControl(CacheControl.noCache().cachePublic());
    }
}
