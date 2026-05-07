package com.cosmetics.cosmetics_store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối đến thư mục chứa ảnh
        Path imageDir = Paths.get("src/main/resources/static/images");
        String imagePath = imageDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + imagePath + "/");
    }
}